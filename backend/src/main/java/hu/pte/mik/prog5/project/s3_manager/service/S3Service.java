package hu.pte.mik.prog5.project.s3_manager.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import hu.pte.mik.prog5.project.s3_manager.Repository.S3EndpointRepository;
import hu.pte.mik.prog5.project.s3_manager.entity.S3Endpoint;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {
    private final S3EndpointRepository s3EndpointRepository;

    @Autowired
    public S3Service(S3EndpointRepository s3EndpointRepository) {
        this.s3EndpointRepository = s3EndpointRepository;
    }

    public List<String> listBucketContents(Long id) throws Exception {
        S3Endpoint endpoint = s3EndpointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("S3 endpoint not found for id: " + id));

        AmazonS3 amazonS3 = createAmazonS3Client(endpoint);

        ListObjectsV2Result result = amazonS3.listObjectsV2(endpoint.getBucketName());
        return result.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }

    public void deleteFile(Long id, String fileName) throws Exception {
        S3Endpoint endpoint = s3EndpointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("S3 endpoint not found for id: " + id));

        AmazonS3 amazonS3 = createAmazonS3Client(endpoint);

        try {
            amazonS3.deleteObject(endpoint.getBucketName(), fileName);
        } catch (AmazonS3Exception e) {
            throw new Exception("Failed to delete file: " + e.getMessage());
        }
    }

    public String uploadFile(Long id, MultipartFile file) throws Exception {
        S3Endpoint endpoint = s3EndpointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("S3 endpoint not found for id: " + id));

        AmazonS3 amazonS3 = createAmazonS3Client(endpoint);

        try {
            // Convert MultipartFile to File
            File convertedFile = convertMultiPartToFile(file);
            String fileName = generateFileName(file);

            // Upload to S3
            amazonS3.putObject(new PutObjectRequest(endpoint.getBucketName(), fileName, convertedFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            // Clean up the temporary file
            convertedFile.delete();

            return fileName;
        } catch (AmazonServiceException e) {
            throw new Exception("Failed to upload file: " + e.getMessage());
        }
    }

    public void downloadFile(Long id, String fileName, HttpServletResponse response) throws Exception {
        S3Endpoint endpoint = s3EndpointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("S3 endpoint not found for id: " + id));

        AmazonS3 amazonS3 = createAmazonS3Client(endpoint);

        try {
            S3Object s3Object = amazonS3.getObject(endpoint.getBucketName(), fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            response.setContentType(s3Object.getObjectMetadata().getContentType());
            response.setContentLengthLong(s3Object.getObjectMetadata().getContentLength());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            IOUtils.copy(inputStream, response.getOutputStream());
            inputStream.close();
            response.getOutputStream().close();
        } catch (AmazonServiceException e) {
            throw new Exception("Failed to download file: " + e.getMessage());
        } catch (IOException e) {
            throw new Exception("IO error while downloading file: " + e.getMessage());
        }
    }


    private AmazonS3 createAmazonS3Client(S3Endpoint endpoint) {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(endpoint.getAccessKey(), endpoint.getSecretKey());

        String protocol = endpoint.getEndpointUrl().split("://")[0].toLowerCase();

        Protocol awsProtocol = protocol.equals("http") ? Protocol.HTTP : Protocol.HTTPS;

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint.getEndpointUrl(), endpoint.getRegion())
                )
                .enablePathStyleAccess()
                .disableChunkedEncoding()
                .withClientConfiguration(
                        new ClientConfiguration().withProtocol(awsProtocol)
                )
                .build();
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }

    private String generateFileName(MultipartFile file) {
        return new Date().getTime() + "-" + file.getOriginalFilename().replace(" ", "_");
    }
}

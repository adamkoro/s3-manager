package hu.pte.mik.prog5.project.s3_manager.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class S3EndpointDTO {
    private Long id;
    private String endpointUrl;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region;
    private String owner;
}

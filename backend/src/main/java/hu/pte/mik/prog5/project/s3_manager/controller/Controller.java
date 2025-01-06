package hu.pte.mik.prog5.project.s3_manager.controller;

import hu.pte.mik.prog5.project.s3_manager.entity.S3Endpoint;
import hu.pte.mik.prog5.project.s3_manager.Repository.S3EndpointRepository;
import hu.pte.mik.prog5.project.s3_manager.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/s3")
public class Controller {

    private final S3EndpointRepository s3EndpointRepository;
    private final S3Service s3Service;

    @Autowired
    public Controller (S3EndpointRepository s3EndpointRepository) {
        this.s3EndpointRepository = s3EndpointRepository;
        this.s3Service = new S3Service(s3EndpointRepository);
    }

    @PostMapping
    public ResponseEntity<S3Endpoint> createS3Endpoint(@Validated @RequestBody S3Endpoint s3Endpoint, Principal principal) {
        try {
            String username = principal.getName();
            s3Endpoint.setOwner(username);
            S3Endpoint savedEndpoint = s3EndpointRepository.save(s3Endpoint);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEndpoint);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping
    public ResponseEntity<List<S3Endpoint>> getAllS3Endpoints(Principal principal){
        try {
            String username = principal.getName();
            List<S3Endpoint> endpoints = s3EndpointRepository.findByOwner(username);
            return ResponseEntity.status(HttpStatus.OK).body(endpoints);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<String>> listBucketContents(@PathVariable Long id) {
        try {
            List<String> contents = s3Service.listBucketContents(id);
            return ResponseEntity.ok(contents);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/download/**")
    public void downloadFile(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = request.getRequestURI().split(request.getContextPath() + "/" + id + "/download/")[1];
            s3Service.downloadFile(id, fileName, response);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<S3Endpoint> updateS3Storage(@PathVariable Long id, @RequestBody S3Endpoint updatedStorage, Principal principal) {
        try {
            Optional<S3Endpoint> existingStorage = s3EndpointRepository.findById(id);
            if (existingStorage.isEmpty()) {
                log.info("S3 storage not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
            S3Endpoint storage = existingStorage.get();
            storage.setEndpointUrl(updatedStorage.getEndpointUrl());
            storage.setAccessKey(updatedStorage.getAccessKey());
            storage.setSecretKey(updatedStorage.getSecretKey());
            storage.setBucketName(updatedStorage.getBucketName());
            storage.setRegion(updatedStorage.getRegion());
            storage.setOwner(principal.getName());
            S3Endpoint savedStorage = s3EndpointRepository.save(storage);
            log.info("Updated s3 endpoint with id: {}", id);
            return ResponseEntity.ok(savedStorage);
        } catch (Exception e) {
            log.error("Error updating S3 storage with id {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String fileName = s3Service.uploadFile(id, file);
            log.info("File uploaded: {}, id: {} ", fileName, id );
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteS3Endpoint(@PathVariable Long id) {
        try {
            Optional<S3Endpoint> storage = s3EndpointRepository.findById(id);
            if (storage.isEmpty()) {
                log.info("S3 storage not found with id: {}", id);
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("The specified S3 storage does not exist");
            }
            s3EndpointRepository.delete(storage.get());
            log.info("Deleted s3 endpoint with id: {}", id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("The specified S3 storage was successfully deleted");
        } catch (Exception e) {
            log.error("Error deleting S3 storage with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete S3 storage: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id, @PathVariable String fileName) {
        try {
            s3Service.deleteFile(id, fileName);
            log.info("Deleted file on endpoint id: {}, filename: {}", id, fileName);
            return ResponseEntity.ok(fileName + " deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file: " + e.getMessage());
        }
    }
}

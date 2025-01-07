package hu.pte.mik.prog5.project.s3_manager.controller;

import hu.pte.mik.prog5.project.s3_manager.dto.Mapper;
import hu.pte.mik.prog5.project.s3_manager.dto.S3EndpointDTO;
import hu.pte.mik.prog5.project.s3_manager.entity.S3Endpoint;
import hu.pte.mik.prog5.project.s3_manager.repository.S3EndpointRepository;
import hu.pte.mik.prog5.project.s3_manager.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final Mapper s3EndpointMapper;

    @Autowired
    public Controller(S3EndpointRepository s3EndpointRepository, S3Service s3Service, Mapper s3Mapper) {
        this.s3EndpointRepository = s3EndpointRepository;
        this.s3Service = s3Service;
        this.s3EndpointMapper = s3Mapper;
    }

    @PostMapping
    public ResponseEntity<S3EndpointDTO> createS3Endpoint(@Validated @RequestBody S3EndpointDTO s3EndpointDTO, Principal principal) {
        try {
            String username = principal.getName();
            S3Endpoint s3Endpoint = s3EndpointMapper.mapToEntity(s3EndpointDTO);
            s3Endpoint.setOwner(username);
            S3Endpoint savedEndpoint = s3EndpointRepository.save(s3Endpoint);
            S3EndpointDTO savedDto = s3EndpointMapper.mapToDto(savedEndpoint);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
        } catch (Exception e) {
            log.error("Error creating S3 endpoint: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<S3EndpointDTO>> getAllS3Endpoints(Principal principal) {
        try {
            String username = principal.getName();
            List<S3Endpoint> endpoints = s3EndpointRepository.findByOwner(username);

            List<S3EndpointDTO> dtoList = endpoints.stream()
                    .map(s3EndpointMapper::mapToDto)
                    .toList();
            return ResponseEntity.status(HttpStatus.OK).body(dtoList);
        } catch (Exception e) {
            log.error("Error retrieving S3 endpoints: {}", e.getMessage());
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
    public ResponseEntity<S3EndpointDTO> updateS3Endpoint(@PathVariable Long id, @RequestBody S3EndpointDTO updatedDto, Principal principal) {
        try {
            Optional<S3Endpoint> existingOpt = s3EndpointRepository.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            S3Endpoint existing = existingOpt.get();
            existing.setEndpointUrl(updatedDto.getEndpointUrl());
            existing.setAccessKey(updatedDto.getAccessKey());
            existing.setSecretKey(updatedDto.getSecretKey());
            existing.setBucketName(updatedDto.getBucketName());
            existing.setRegion(updatedDto.getRegion());
            existing.setOwner(principal.getName());
            S3Endpoint saved = s3EndpointRepository.save(existing);
            S3EndpointDTO savedDto = s3EndpointMapper.mapToDto(saved);
            return ResponseEntity.ok(savedDto);
        } catch (Exception e) {
            log.error("Error updating S3 endpoint with id {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String fileName = s3Service.uploadFile(id, file);
            log.info("File uploaded: {}, id: {}", fileName, id);
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteS3Endpoint(@PathVariable Long id) {
        try {
            Optional<S3Endpoint> storage = s3EndpointRepository.findById(id);
            if (storage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("S3 storage not found");
            }
            s3EndpointRepository.delete(storage.get());
            return ResponseEntity.ok("S3 storage deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting S3 storage with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete S3 storage: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id, @PathVariable String fileName) {
        try {
            s3Service.deleteFile(id, fileName);
            return ResponseEntity.ok(fileName + " deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting file {} on endpoint {}: {}", fileName, id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file: " + e.getMessage());
        }
    }
}

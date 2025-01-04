package hu.pte.mik.prog5.project.s3_manager.controller;

import hu.pte.mik.prog5.project.s3_manager.entity.S3Endpoint;
import hu.pte.mik.prog5.project.s3_manager.Repository.S3EndpointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/s3")
public class Controller {

    private final S3EndpointRepository s3EndpointRepository;

    @Autowired
    public Controller (S3EndpointRepository s3EndpointRepository) {
        this.s3EndpointRepository = s3EndpointRepository;
    }


//    @PostMapping
//    public ResponseEntity<S3Endpoint> createS3Endpoint(@Validated @RequestBody S3Endpoint s3Endpoint) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        User user = userRepository.findByName(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        s3Endpoint.setOwner(user);
//        S3Endpoint savedEndpoint = s3EndpointRepository.save(s3Endpoint);
//
//        return ResponseEntity.ok(savedEndpoint);
//    }

    @GetMapping
    public ResponseEntity<List<S3Endpoint>> getAllS3Endpoints(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        log.info(username);


        List<S3Endpoint> endpoints = s3EndpointRepository.findByOwner(username);
        return ResponseEntity.ok(endpoints);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<S3Endpoint> getS3EndpointById(@PathVariable Long id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        User user = userRepository.findByName(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        S3Endpoint endpoint = s3EndpointRepository.findByIdAndOwner(id, user);
//
//        return ResponseEntity.ok(endpoint);
//    }

//    @PutMapping("/{id}")
//    public ResponseEntity<S3Endpoint> updateS3Endpoint(@PathVariable Long id, @Validated @RequestBody S3Endpoint updatedEndpoint) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        User user = userRepository.findByName(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        S3Endpoint existingEndpoint = s3EndpointRepository.findByIdAndOwner(id, user);
//
//        existingEndpoint.setEndpointUrl(updatedEndpoint.getEndpointUrl());
//        existingEndpoint.setAccessKey(updatedEndpoint.getAccessKey());
//        existingEndpoint.setSecretKey(updatedEndpoint.getSecretKey());
//
//        S3Endpoint savedEndpoint = s3EndpointRepository.save(existingEndpoint);
//        return ResponseEntity.ok(savedEndpoint);
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteS3Endpoint(@PathVariable Long id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        User user = userRepository.findByName(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        S3Endpoint endpoint = s3EndpointRepository.findByIdAndOwner(id, user);
//
//        s3EndpointRepository.delete(endpoint);
//        return ResponseEntity.noContent().build();
//    }
}

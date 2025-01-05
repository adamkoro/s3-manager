package hu.pte.mik.prog5.project.s3_manager.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "s3_endpoint")
@Data
public class S3Endpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String endpointUrl;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region;
    private String owner;
}

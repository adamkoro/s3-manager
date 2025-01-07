package hu.pte.mik.prog5.project.s3_manager.dto;

import hu.pte.mik.prog5.project.s3_manager.entity.S3Endpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {
    public S3EndpointDTO mapToDto(S3Endpoint entity) {
        if (entity == null) {
            return null;
        }
        S3EndpointDTO dto = new S3EndpointDTO();
        dto.setId(entity.getId());
        dto.setEndpointUrl(entity.getEndpointUrl());
        dto.setAccessKey(entity.getAccessKey());
        dto.setSecretKey(entity.getSecretKey());
        dto.setBucketName(entity.getBucketName());
        dto.setRegion(entity.getRegion());
        dto.setOwner(entity.getOwner());
        return dto;
    }

    public S3Endpoint mapToEntity(S3EndpointDTO dto) {
        if (dto == null) {
            return null;
        }
        S3Endpoint entity = new S3Endpoint();
        entity.setId(dto.getId());
        entity.setEndpointUrl(dto.getEndpointUrl());
        entity.setAccessKey(dto.getAccessKey());
        entity.setSecretKey(dto.getSecretKey());
        entity.setBucketName(dto.getBucketName());
        entity.setRegion(dto.getRegion());
        entity.setOwner(dto.getOwner());
        return entity;
    }
}

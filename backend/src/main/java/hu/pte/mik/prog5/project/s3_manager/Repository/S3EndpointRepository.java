package hu.pte.mik.prog5.project.s3_manager.Repository;

import hu.pte.mik.prog5.project.s3_manager.entity.S3Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface S3EndpointRepository extends JpaRepository<S3Endpoint, Long> {

    List<S3Endpoint> findByOwner(String owner);

}


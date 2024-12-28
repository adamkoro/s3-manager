package hu.pte.mik.prog5.project.s3_manager.Repository;

import hu.pte.mik.prog5.project.s3_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByName(String name);
}

package yevhen.synii.admin_panel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import yevhen.synii.admin_panel.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(Long id);
}

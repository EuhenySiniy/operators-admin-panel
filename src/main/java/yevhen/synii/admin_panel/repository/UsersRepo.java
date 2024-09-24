package yevhen.synii.admin_panel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yevhen.synii.admin_panel.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(Long id);

    @Modifying
    @Transactional
    @Query(value = "update admin_panel_dev.users set first_name = ?1, last_name = ?2, email = ?3, profile_photo = ?4 where id = ?5",
    nativeQuery = true)
    void changeUserProfile(
            String firstName,
            String lastName,
            String email,
            String profilePhoto,
            Long id);
}

package yevhen.synii.admin_panel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yevhen.synii.admin_panel.entity.TokenEntity;

@Repository
public interface TokensRepo extends JpaRepository<TokenEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "update admin_panel_dev.tokens set expired = true where user_id = ? ",
            nativeQuery = true)
    void killToken(Long userId);
}

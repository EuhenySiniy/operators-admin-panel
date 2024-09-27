package yevhen.synii.admin_panel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yevhen.synii.admin_panel.entity.EventEntity;

import java.util.List;

@Repository
public interface EventsRepo extends JpaRepository<EventEntity, Long> {
    @Query(value = "select * from admin_panel_dev.events where id = ?",
            nativeQuery = true)
    List<EventEntity> getUserEvents(Long id);
}

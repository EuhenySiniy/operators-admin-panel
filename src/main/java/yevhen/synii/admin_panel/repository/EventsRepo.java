package yevhen.synii.admin_panel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yevhen.synii.admin_panel.entity.EventEntity;

@Repository
public interface EventsRepo extends JpaRepository<EventEntity, Long> {
}

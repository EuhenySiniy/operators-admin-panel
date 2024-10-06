package yevhen.synii.admin_panel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yevhen.synii.admin_panel.entity.EventEntity;
import yevhen.synii.admin_panel.service.EventService;

import java.util.List;

@Repository
public interface EventsRepo extends JpaRepository<EventEntity, Long> {
    @Query(value = "select * from admin_panel_dev.events where id = ?",
            nativeQuery = true)
    List<EventEntity> getUserEvents(Long id);

    @Query(value = "select distinct " +
            "e.id, " +
            "e.event_name, " +
            "e.event_description," +
            "e.event_link, " +
            "e.event_date_time," +
            "e.facilitator_id, " +
            "e.created_at, " +
            "e.updated_at " +
            "from admin_panel_dev.events_to_users as eto " +
            "join admin_panel_dev.events as e on eto.event_id = e.id " +
            "where eto.user_id = ? " +
            "and e.event_date_time >= current_timestamp " +
            "order by e.event_date_time ASC " +
            "limit 5 ",
            nativeQuery = true)
    List<EventEntity> getUserEventsByUserId(Long id);

    @Query(value = "select * from admin_panel_dev.events where id = ? ",
            nativeQuery = true)
    EventEntity getEventById(Long id);
}

package yevhen.synii.admin_panel.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import yevhen.synii.admin_panel.entity.enums.UserRole;
import yevhen.synii.admin_panel.entity.enums.UserStatus;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity implements UserDetails {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    @Enumerated(EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "admin_panel_dev.user_role")
    private UserRole role;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "active_time")
    private float activeTime;

    @Column(name = "quality_assurance")
    private float qualityAssurance;

    @Column(name = "processing_speed")
    private float processingSpeed;

    @Column(name = "knowledge_quality")
    private float knowledgeQuality;

    @Column(name = "total_kpi")
    private float totalKpi;

    @Enumerated(EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "user_status")
    private UserStatus status;

    @Column(name = "pass")
    private String password;

    private String salt;

    @Column(name = "next_shift_at")
    private Timestamp nextShift;

    @Column(name = "start_work_at")
    private Timestamp startedWork;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

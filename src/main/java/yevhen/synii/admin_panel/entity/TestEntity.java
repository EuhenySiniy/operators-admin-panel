package yevhen.synii.admin_panel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import yevhen.synii.admin_panel.entity.enums.TestType;

import java.sql.Timestamp;

@Entity
@Table(name = "tests")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity extends BaseEntity {
    @Column(name = "test_type")
    private TestType testType;
    @Column(name = "questions")
    private String questions;
    @Column(name = "correct_answers")
    private String correctAnswers;
    @Column(name = "expired_at")
    private Timestamp expiredAt;
}

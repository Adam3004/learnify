package com.brightpath.learnify.persistance.note.date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "note_date_statistics")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DateStatisticsEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "viewed_at")
    private OffsetDateTime viewedAt;
}

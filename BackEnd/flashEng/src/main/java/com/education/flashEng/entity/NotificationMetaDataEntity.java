package com.education.flashEng.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification_meta_data")
public class NotificationMetaDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`key`", nullable = false)
    private String key;

    @Column(name = "value", nullable = false)
    private String value;

    @OneToMany(mappedBy = "notificationMetaDataEntity")
    private List<NotificationEntity> notificationEntityList;
}

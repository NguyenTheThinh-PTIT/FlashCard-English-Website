package com.education.flashEng.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

@Entity
@Table(name = "classes")
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "classEntity")
    private List<SetEntity> setsEntityList;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassInvitationEntity> classInvitationEntityList;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassSetRequestEntity> classSetRequestEntityList;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassMemberEntity> classMemberEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity")
    private List<ClassJoinRequestEntity> classJoinRequestEntityList;

}

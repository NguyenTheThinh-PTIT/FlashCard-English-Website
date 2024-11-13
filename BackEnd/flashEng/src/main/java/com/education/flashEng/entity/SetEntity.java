package com.education.flashEng.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "sets")
public class SetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "privacy_status", nullable = false)
    private String privacyStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @OneToMany(mappedBy = "setEntity", cascade = CascadeType.ALL)
    private List<WordEntity> wordsEntityList;

    @OneToMany(mappedBy = "setEntity", cascade = CascadeType.ALL)
    private List<ClassSetRequestEntity> classSetRequestEntityList;

}

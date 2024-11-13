package com.education.flashEng.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@Entity
@Table(name = "words")
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "ipa", nullable = false)
    private String ipa;

    @Column(name = "definition", nullable = false)
    private String definition;

    @Column(name = "example", nullable = false)
    private String example;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "image_public_id", nullable = false)
    private String imagePublicId;

    @Column(name = "audio", nullable = false)
    private String audio;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private SetEntity setEntity;

    @OneToMany(mappedBy = "wordEntity")
    private List<StudySessionEntity> studySessionEntityList;
}

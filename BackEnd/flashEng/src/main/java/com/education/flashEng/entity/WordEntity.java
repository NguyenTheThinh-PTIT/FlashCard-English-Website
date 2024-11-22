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

    @Lob
    @Column(name = "definition", nullable = false, columnDefinition = "LONGTEXT")
    private String definition;

    @Lob
    @Column(name = "example", nullable = false, columnDefinition = "LONGTEXT")
    private String example;

    @Lob
    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;

    @Lob
    @Column(name = "image_public_id", columnDefinition = "LONGTEXT")
    private String imagePublicId;

    @Lob
    @Column(name = "audio", columnDefinition = "LONGTEXT")
    private String audio;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private SetEntity setEntity;

    @OneToMany(mappedBy = "wordEntity", cascade = CascadeType.ALL)
    private List<StudySessionEntity> studySessionEntityList;
}

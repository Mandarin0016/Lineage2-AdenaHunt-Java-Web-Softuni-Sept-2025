package main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private PlayerClass playerClass;

    private String bannerImg;

    @ManyToOne
    private Party party;

    private int level;

    private double health;

    private double attack;

    private double defense;

    private double xp;

    private int adena;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}


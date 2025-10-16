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
public class Mob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private int level;

    private double health;

    private double attack;

    private double defense;

    private boolean alive;

    private int adenaDrop;

    private int xpDrop;

    @Enumerated(EnumType.STRING)
    private MobType type;

    private String imageUrl;

    private String spawnArea;

    private String description;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}


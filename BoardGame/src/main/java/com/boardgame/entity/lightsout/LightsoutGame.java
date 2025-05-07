package com.boardgame.entity.lightsout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LightsoutGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private LightsoutPlayer player;

    @ElementCollection
    @CollectionTable(name = "lightsout_grid", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "row_data")
    private List<String> grid;

    private boolean isGameOver;
}

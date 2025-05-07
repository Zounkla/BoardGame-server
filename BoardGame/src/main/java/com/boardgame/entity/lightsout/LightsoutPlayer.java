package com.boardgame.entity.lightsout;

import com.boardgame.entity.platform.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LightsoutPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "game_id")
    private LightsoutGame game;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
}

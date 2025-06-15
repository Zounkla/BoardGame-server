package com.boardgame.entity.tictactoe;

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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "lobby_id"}),
                @UniqueConstraint(columnNames = {"user_id", "game_id"})
        }
)
public class TictactoePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private TictactoeGame game;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "lobby_id")
    private TictactoeLobby lobby;
}

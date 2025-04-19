package com.boardgame.entity.yathzee;

import com.boardgame.utils.yathzee.YathzeeConstants;
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
public class YathzeeGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YathzeePlayer> players;

    @ManyToOne
    @JoinColumn(name = "active_player_id")
    private YathzeePlayer activePlayer;

    @ElementCollection
    @CollectionTable(name = "yathzee_dices", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "dice_value")
    private List<Integer> dices;

    private int remainingRolls = YathzeeConstants.MAX_ROLLS;
}

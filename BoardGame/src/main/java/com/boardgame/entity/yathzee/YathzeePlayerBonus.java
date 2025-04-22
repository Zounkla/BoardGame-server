package com.boardgame.entity.yathzee;

import com.boardgame.enums.yathzee.YathzeeBonus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "bonus"})
})
public class YathzeePlayerBonus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private YathzeeBonus bonus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private YathzeePlayer player;

    private int score;
}

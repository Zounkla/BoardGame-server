package com.boardgame.repository.yathzee;

import com.boardgame.entity.yathzee.YathzeePlayer;
import com.boardgame.entity.yathzee.YathzeePlayerBonus;
import com.boardgame.enums.yathzee.YathzeeBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YathzeePlayerBonusRepository extends JpaRepository<YathzeePlayerBonus, Long> {
    boolean existsYathzeePlayerBonusByPlayerAndBonus(YathzeePlayer player, YathzeeBonus bonus);
}

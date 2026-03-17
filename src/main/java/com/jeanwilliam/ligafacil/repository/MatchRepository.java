package com.jeanwilliam.ligafacil.repository;

import com.jeanwilliam.ligafacil.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByChampionshipId(Long championshipId);

    List<Match> findByChampionshipIdAndRound(Long championshipId, Integer round);
}

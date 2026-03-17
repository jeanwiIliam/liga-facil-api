package com.jeanwilliam.ligafacil.repository;

import com.jeanwilliam.ligafacil.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByChampionshipIdOrderByNameAsc(Long championshipId);
}

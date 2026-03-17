package com.jeanwilliam.ligafacil.dto.response;

import com.jeanwilliam.ligafacil.entity.Match;

public record MatchResponse(
        Long id,
        Integer round,
        String homeTeam,
        String awayTeam,
        Integer homeScore,
        Integer awayScore
) {
    public MatchResponse(Match match){
        this(
                match.getId(),
                match.getRound(),
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName(),
                match.getHomeScore(),
                match.getAwayScore()
        );
    }
}

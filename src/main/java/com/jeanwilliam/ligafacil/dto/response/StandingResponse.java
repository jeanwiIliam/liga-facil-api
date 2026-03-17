package com.jeanwilliam.ligafacil.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StandingResponse {
    private Long teamId;
    private Integer position;
    private String teamName;

    private Integer points = 0;
    private Integer matchesPlayed = 0;
    private Integer won = 0;
    private Integer drawn = 0;
    private Integer lost = 0;

    private Integer goalsFor = 0;
    private Integer goalsAgainst = 0;
    private Integer goalsDifference = 0;
}

package com.jeanwilliam.ligafacil.dto.response;

import com.jeanwilliam.ligafacil.entity.Championship;

public record ChampionshipResponse(
        Long id,
        String name) {
    public ChampionshipResponse(Championship championship){
        this(championship.getId(), championship.getName());
    }
}

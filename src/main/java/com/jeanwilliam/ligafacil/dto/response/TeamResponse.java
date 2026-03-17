package com.jeanwilliam.ligafacil.dto.response;

import com.jeanwilliam.ligafacil.entity.Team;

public record TeamResponse(Long id, String name, String image) {
    public TeamResponse(Team team) {
        this(team.getId(), team.getName(), team.getImage());
    }
}

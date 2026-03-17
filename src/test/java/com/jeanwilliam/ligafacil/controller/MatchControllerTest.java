package com.jeanwilliam.ligafacil.controller;

import com.jeanwilliam.ligafacil.dto.response.MatchResponse;
import com.jeanwilliam.ligafacil.entity.Match;
import com.jeanwilliam.ligafacil.entity.Team;
import com.jeanwilliam.ligafacil.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MatchControllerTest{

    @InjectMocks
    private MatchController controller;

    @Mock
    private MatchService matchService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldGenerateMatches() throws Exception {
        MatchResponse response = new MatchResponse(1L, 1, "Team A", "Team B", null, null);

        when(matchService.generateMatches(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(post("/championships/1/generate-matches"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].round").value(1))
                .andExpect(jsonPath("$[0].homeTeam").value("Team A"))
                .andExpect(jsonPath("$[0].awayTeam").value("Team B"));

        verify(matchService).generateMatches(1L);
        verifyNoMoreInteractions(matchService);
    }

    @Test
    void shouldReturnMatches() throws Exception {
        Team home = createTeam("Team A");
        Team away = createTeam("Team B");
        Match match = createMatch(1L, 1, home, away);

        when(matchService.getMatches(1L, null))
                .thenReturn(List.of(match));

        mockMvc.perform(get("/championships/1/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].round").value(1))
                .andExpect(jsonPath("$[0].homeTeam").value("Team A"))
                .andExpect(jsonPath("$[0].awayTeam").value("Team B"));

        verify(matchService).getMatches(1L, null);
        verifyNoMoreInteractions(matchService);
    }

    @Test
    void shouldReturnMatchById() throws Exception {
        Team home = createTeam("Team A");
        Team away = createTeam("Team B");
        Match match = createMatch(1L, 1, home, away);

        when(matchService.findById(1L))
                .thenReturn(match);

        mockMvc.perform(get("/matches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.round").value(1))
                .andExpect(jsonPath("$.homeTeam").value("Team A"))
                .andExpect(jsonPath("$.awayTeam").value("Team B"));

        verify(matchService).findById(1L);
        verifyNoMoreInteractions(matchService);
    }

    @Test
    void shouldUpdateMatchScore() throws Exception {
        Team home = createTeam("Team A");
        Team away = createTeam("Team B");
        Match match = createMatch(1L, 1, home, away);
        match.setHomeScore(2);
        match.setAwayScore(1);

        when(matchService.updateScore(1L, 2, 1))
                .thenReturn(match);

        String requestJson = """
        {
            "homeScore": 2,
            "awayScore": 1
        }
        """;

        mockMvc.perform(patch("/matches/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.homeScore").value(2))
                .andExpect(jsonPath("$.awayScore").value(1))
                .andExpect(jsonPath("$.homeTeam").value("Team A"))
                .andExpect(jsonPath("$.awayTeam").value("Team B"));

        verify(matchService).updateScore(1L, 2, 1);
        verifyNoMoreInteractions(matchService);
    }

    private Team createTeam(String name){
        Team team = new Team();
        team.setName(name);
        return team;
    }

    private Match createMatch(Long id, Integer round, Team homeTeam, Team awayTeam){
        Match match = new Match();
        match.setId(id);
        match.setRound(round);
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        return match;
    }
}

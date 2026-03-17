package com.jeanwilliam.ligafacil.controller;

import com.jeanwilliam.ligafacil.dto.response.ChampionshipResponse;
import com.jeanwilliam.ligafacil.dto.response.StandingResponse;
import com.jeanwilliam.ligafacil.entity.Championship;
import com.jeanwilliam.ligafacil.service.ChampionshipService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChampionshipControllerTest {

    @InjectMocks
    private ChampionshipController controller;

    @Mock
    private ChampionshipService championshipService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldReturnAllChampionshipsWithData() throws Exception {
        Championship championship = createChampionship(1L, "Brasileirão");

        when(championshipService.findAll())
                .thenReturn(List.of(championship));

        mockMvc.perform(get("/championships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Brasileirão"));

        verify(championshipService).findAll();
        verifyNoMoreInteractions(championshipService);
    }

    @Test
    void shouldReturnChampionshipById() throws Exception {
        Championship championship = createChampionship(1L, "Brasileirão");

        when(championshipService.findById(1L))
                .thenReturn(championship);

        mockMvc.perform(get("/championships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Brasileirão"));

        verify(championshipService).findById(1L);
        verifyNoMoreInteractions(championshipService);
    }

    @Test
    void shouldCreateChampionship() throws Exception {
        ChampionshipResponse response = new ChampionshipResponse(1L, "Brasileirão");

        when(championshipService.create(any()))
                .thenReturn(response);

        String requestJson = """
        {
            "name": "Brasileirão"
        }
        """;

        mockMvc.perform(post("/championships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Brasileirão"));

        verify(championshipService).create(any());
        verifyNoMoreInteractions(championshipService);
    }

    @Test
    void shouldReturnStandings() throws Exception {
        StandingResponse standing = new StandingResponse();
        standing.setTeamId(1L);
        standing.setTeamName("Team A");
        standing.setPoints(10);

        when(championshipService.getTable(1L))
                .thenReturn(List.of(standing));

        mockMvc.perform(get("/championships/1/standings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value(1))
                .andExpect(jsonPath("$[0].teamName").value("Team A"))
                .andExpect(jsonPath("$[0].points").value(10));

        verify(championshipService).getTable(1L);
        verifyNoMoreInteractions(championshipService);
    }

    private Championship createChampionship(Long id, String name){
        Championship championship = new Championship();
        championship.setId(id);
        championship.setName(name);
        return championship;
    }
}

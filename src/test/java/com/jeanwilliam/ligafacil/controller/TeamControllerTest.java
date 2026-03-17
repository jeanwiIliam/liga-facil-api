package com.jeanwilliam.ligafacil.controller;

import com.jeanwilliam.ligafacil.dto.response.TeamResponse;
import com.jeanwilliam.ligafacil.entity.Team;
import com.jeanwilliam.ligafacil.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @InjectMocks
    private TeamController controller;

    @Mock
    private TeamService teamService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }
    @Test
    void shouldCreateTeam() throws Exception {
        TeamResponse response = new TeamResponse(1L, "Team A", null);

        MockMultipartFile teamRequest = new MockMultipartFile(
                "teamRequest",
                "",
                "application/json",
                """
                {
                    "name": "Team A"
                }
                """.getBytes()
        );

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image".getBytes()
        );

        when(teamService.create(eq(1L), any(), any()))
                .thenReturn(response);

        mockMvc.perform(multipart("/championships/1/teams")
                        .file(teamRequest)
                        .file(image))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Team A"));

        verify(teamService).create(eq(1L), any(), any());
        verifyNoMoreInteractions(teamService);
    }

    @Test
    void shouldReturnTeamsByChampionship() throws Exception {
        Team team = new Team();
        team.setId(1L);
        team.setName("Team A");

        when(teamService.findByChampionship(1L))
                .thenReturn(List.of(team));

        mockMvc.perform(get("/championships/1/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Team A"));

        verify(teamService).findByChampionship(1L);
        verifyNoMoreInteractions(teamService);
    }
}

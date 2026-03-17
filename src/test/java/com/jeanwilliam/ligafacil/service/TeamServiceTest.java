package com.jeanwilliam.ligafacil.service;

import com.jeanwilliam.ligafacil.dto.request.TeamRequest;
import com.jeanwilliam.ligafacil.dto.response.TeamResponse;
import com.jeanwilliam.ligafacil.entity.Championship;
import com.jeanwilliam.ligafacil.entity.Team;
import com.jeanwilliam.ligafacil.repository.ChampionshipRepository;

import com.jeanwilliam.ligafacil.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ChampionshipRepository championshipRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    void shouldCreateTeamSuccessfully() {

        Championship championship = new Championship();
        championship.setId(1L);

        TeamRequest request = new TeamRequest("Team A");

        when(championshipRepository.findById(1L))
                .thenReturn(Optional.of(championship));

        TeamResponse response = teamService.create(1L, request, null);

        assertNotNull(response);

        verify(championshipRepository).findById(1L);
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void shouldThrowExceptionWhenChampionshipNotFound() {

        TeamRequest request = new TeamRequest("Team A");

        when(championshipRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> teamService.create(1L, request, null)
        );

        assertEquals("Championship not found", exception.getMessage());

        verify(championshipRepository).findById(1L);
        verify(teamRepository, never()).save(any());
    }

    @Test
    void shouldReturnTeamsByChampionship() {

        Team team1 = createTeam(1L, "Team A");
        Team team2 = createTeam(2L, "Team B");

        List<Team> teams = List.of(team1, team2);

        when(teamRepository.findByChampionshipIdOrderByNameAsc(1L))
                .thenReturn(teams);

        List<Team> result = teamService.findByChampionship(1L);

        assertEquals(2, result.size());
        assertEquals("Team A", result.get(0).getName());
        assertEquals("Team B", result.get(1).getName());

        verify(teamRepository).findByChampionshipIdOrderByNameAsc(1L);
    }

    @Test
    void shouldUpdateTeamSuccessfully() throws Exception {

        Team team = createTeam(1L, "Old name");

        TeamRequest request = new TeamRequest("New Name");

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "fake-image".getBytes()
        );

        when(teamRepository.findById(1L))
                .thenReturn(Optional.of(team));

        TeamResponse response = teamService.update(1L, request, image);

        assertNotNull(response);
        assertEquals("New Name", team.getName());
        assertNotNull(team.getImage());

        verify(teamRepository).findById(1L);
        verify(teamRepository).save(team);
    }

    private Team createTeam(Long id, String name) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        return team;
    }

}

package com.jeanwilliam.ligafacil.service;

import com.jeanwilliam.ligafacil.dto.request.ChampionshipRequest;
import com.jeanwilliam.ligafacil.dto.response.ChampionshipResponse;
import com.jeanwilliam.ligafacil.dto.response.StandingResponse;
import com.jeanwilliam.ligafacil.entity.Championship;
import com.jeanwilliam.ligafacil.entity.Match;
import com.jeanwilliam.ligafacil.entity.Team;
import com.jeanwilliam.ligafacil.repository.ChampionshipRepository;
import com.jeanwilliam.ligafacil.repository.MatchRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChampionshipServiceTest {

    @Mock
    private ChampionshipRepository championshipRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private ChampionshipService championshipService;

    @Test
    void shouldCreateChampionshipSuccessfully() {

        ChampionshipRequest request = new ChampionshipRequest("Brasileirão");

        Championship championship = new Championship(request);

        when(championshipRepository.save(any(Championship.class)))
                .thenReturn(championship);

        ChampionshipResponse response = championshipService.create(request);

        assertNotNull(response);

        verify(championshipRepository, times(1))
                .save(any(Championship.class));
    }

    @Test
    void shouldReturnAllChampionships() {

        List<Championship> championships = List.of(
                new Championship(),
                new Championship()
        );

        when(championshipRepository.findAll()).thenReturn(championships);

        List<Championship> result = championshipService.findAll();

        assertEquals(2, result.size());

        verify(championshipRepository).findAll();
    }

    @Test
    void shouldReturnChampionshipWhenIdExists() {

        Championship championship = new Championship();

        when(championshipRepository.findById(1L))
                .thenReturn(java.util.Optional.of(championship));

        Championship result = championshipService.findById(1L);

        assertNotNull(result);

        verify(championshipRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenChampionshipNotFound() {

        when(championshipRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            championshipService.findById(1L);
        });

        verify(championshipRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenMatchesNotFound() {

        when(matchRepository.findByChampionshipId(1L))
                .thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            championshipService.getTable(1L);
        });

        verify(matchRepository).findByChampionshipId(1L);
    }

    @Test
    void shouldCalculateStandingsCorrectly() {

        Team teamA = createTeam(1L, "Team A");
        Team teamB = createTeam(2L, "Team B");

        Match match = createMatch(teamA, teamB, 2, 0);

        List<Match> matches = List.of(match);

        when(matchRepository.findByChampionshipId(1L))
                .thenReturn(matches);

        List<StandingResponse> table = championshipService.getTable(1L);

        assertEquals(2, table.size());

        StandingResponse first = table.get(0);
        StandingResponse second = table.get(1);

        assertEquals("Team A", first.getTeamName());
        assertEquals(3, first.getPoints());
        assertEquals(1, first.getWon());

        assertEquals("Team B", second.getTeamName());
        assertEquals(0, second.getPoints());
        assertEquals(1, second.getLost());

        verify(matchRepository).findByChampionshipId(1L);
    }

    @Test
    void shouldIgnoreMatchesWithoutScore() {

        Team teamA = createTeam(1L, "Team A");
        Team teamB = createTeam(2L, "Team B");

        Match match = createMatch(teamA, teamB, null, null);

        List<Match> matches = List.of(match);

        when(matchRepository.findByChampionshipId(1L))
                .thenReturn(matches);

        List<StandingResponse> table = championshipService.getTable(1L);

        assertEquals(0, table.size());

        verify(matchRepository).findByChampionshipId(1L);
    }

    @Test
    void shouldGiveOnePointForEachTeamWhenMatchIsDraw() {

        Team teamA = createTeam(1L, "Team A");
        Team teamB = createTeam(2L, "Team B");

        Match match = createMatch(teamA, teamB, 1, 1);

        List<Match> matches = List.of(match);

        when(matchRepository.findByChampionshipId(1L))
                .thenReturn(matches);

        List<StandingResponse> table = championshipService.getTable(1L);

        assertEquals(2, table.size());

        StandingResponse first = table.get(0);
        StandingResponse second = table.get(1);

        assertEquals(1, first.getPoints());
        assertEquals(1, second.getPoints());

        assertEquals(1, first.getDrawn());
        assertEquals(1, second.getDrawn());

        verify(matchRepository).findByChampionshipId(1L);
    }

    @Test
    void shouldGoToHeadToHeadTieBreaker() {

        Team teamA = createTeam(1L, "Team A");
        Team teamB = createTeam(2L, "Team B");

        Match match1 = createMatch(teamA, teamB, 1, 0);

        Match match2 = createMatch(teamB, teamA, 2, 1);

        List<Match> matches = List.of(match1, match2);

        when(matchRepository.findByChampionshipId(1L))
                .thenReturn(matches);

        List<StandingResponse> table = championshipService.getTable(1L);

        assertEquals(2, table.size());

        StandingResponse first = table.get(0);
        StandingResponse second = table.get(1);

        assertNotNull(first);
        assertNotNull(second);

        verify(matchRepository).findByChampionshipId(1L);
    }

    private Team createTeam(Long id, String name) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        return team;
    }

    private Match createMatch(Team home, Team away, Integer homeScore, Integer awayScore) {
        Match match = new Match();
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        return match;
    }
}
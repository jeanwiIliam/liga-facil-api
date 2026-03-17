package com.jeanwilliam.ligafacil.service;

import com.jeanwilliam.ligafacil.dto.response.MatchResponse;
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
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private ChampionshipRepository championshipRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    void shouldGenerateMatchesSuccessfully() {

        Championship championship = new Championship();
        championship.setId(1L);

        Team team1 = createTeam(1L, "Team A");
        Team team2 = createTeam(2L, "Team B");
        Team team3 = createTeam(3L, "Team C");
        Team team4 = createTeam(4L, "Team D");

        championship.setTeams(List.of(team1, team2, team3, team4));

        when(championshipRepository.findById(1L))
                .thenReturn(Optional.of(championship));

        List<MatchResponse> matches = matchService.generateMatches(1L);

        assertFalse(matches.isEmpty());

        verify(championshipRepository).findById(1L);
        verify(matchRepository, atLeastOnce()).save(any(Match.class));
    }

    @Test
    void shouldThrowExceptionWhenChampionshipNotFound() {

        when(championshipRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> matchService.generateMatches(1L)
        );

        assertEquals("Championship not found", exception.getMessage());

        verify(championshipRepository).findById(1L);
        verify(matchRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenNumberOfTeamsIsOdd() {

        Championship championship = new Championship();
        championship.setId(1L);

        Team team1 = createTeam(1L, "Team A");
        Team team2 = createTeam(2L, "Team B");
        Team team3 = createTeam(3L, "Team C");

        championship.setTeams(List.of(team1, team2, team3));

        when(championshipRepository.findById(1L))
                .thenReturn(Optional.of(championship));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> matchService.generateMatches(1L)
        );

        assertEquals("Number of teams must be even", exception.getMessage());

        verify(championshipRepository).findById(1L);
        verify(matchRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllMatchesWhenRoundIsNull() {

        Match match1 = new Match();
        match1.setId(1L);

        Match match2 = new Match();
        match2.setId(2L);

        List<Match> matches = List.of(match1, match2);

        when(matchRepository.findByChampionshipId(1L))
                .thenReturn(matches);

        List<Match> result = matchService.getMatches(1L, null);

        assertEquals(2, result.size());

        verify(matchRepository).findByChampionshipId(1L);
        verify(matchRepository, never()).findByChampionshipIdAndRound(any(), any());
    }

    @Test
    void shouldReturnMatchesByRound() {

        Match match1 = new Match();
        match1.setId(1L);
        match1.setRound(1);

        Match match2 = new Match();
        match2.setId(2L);
        match2.setRound(1);

        List<Match> matches = List.of(match1, match2);

        when(matchRepository.findByChampionshipIdAndRound(1L, 1))
                .thenReturn(matches);

        List<Match> result = matchService.getMatches(1L, 1);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRound());
        assertEquals(1, result.get(1).getRound());

        verify(matchRepository).findByChampionshipIdAndRound(1L, 1);
        verify(matchRepository, never()).findByChampionshipId(any());
    }

    @Test
    void shouldReturnMatchWhenIdExists() {

        Match match = new Match();
        match.setId(1L);

        when(matchRepository.findById(1L))
                .thenReturn(Optional.of(match));

        Match result = matchService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(matchRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenMatchNotFound() {

        when(matchRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> matchService.findById(1L)
        );

        assertEquals("Match not found", exception.getMessage());

        verify(matchRepository).findById(1L);
    }


    @Test
    void shouldUpdateScoreSuccessfully() {

        Match match = new Match();
        match.setId(1L);
        match.setHomeScore(0);
        match.setAwayScore(0);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Match updated = matchService.updateScore(1L, 2, 1);

        assertEquals(2, updated.getHomeScore());
        assertEquals(1, updated.getAwayScore());

        verify(matchRepository).findById(1L);
        verify(matchRepository).save(match);
    }

    @Test
    void shouldThrowExceptionWhenMatchNotFoundInUpdateScore() {

        when(matchRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> matchService.updateScore(1L, 2, 1)
        );

        assertEquals("Match not found", exception.getMessage());
        verify(matchRepository).findById(1L);
        verify(matchRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenScoreIsNegative() {

        Match match = new Match();
        match.setId(1L);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> matchService.updateScore(1L, -1, 2)
        );

        assertEquals("Score cannot be negative", exception.getMessage());

        verify(matchRepository).findById(1L);
        verify(matchRepository, never()).save(any());
    }


    private Team createTeam(Long id, String name) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        return team;
    }

}

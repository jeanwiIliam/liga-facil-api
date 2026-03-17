package com.jeanwilliam.ligafacil.service;

import com.jeanwilliam.ligafacil.dto.request.ChampionshipRequest;
import com.jeanwilliam.ligafacil.dto.response.ChampionshipResponse;
import com.jeanwilliam.ligafacil.dto.response.StandingResponse;
import com.jeanwilliam.ligafacil.entity.Championship;
import com.jeanwilliam.ligafacil.entity.Match;
import com.jeanwilliam.ligafacil.entity.Team;
import com.jeanwilliam.ligafacil.repository.ChampionshipRepository;
import com.jeanwilliam.ligafacil.repository.MatchRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChampionshipService {
    private final ChampionshipRepository championshipRepository;
    private final MatchRepository matchRepository;

    public ChampionshipResponse create(ChampionshipRequest request) {
        Championship championship = new Championship(request);
        championshipRepository.save(championship);
        return new ChampionshipResponse(championship);
    }

    public List<Championship> findAll(){
        return championshipRepository.findAll();
    }

    public Championship findById(Long id) {
        return championshipRepository.findById(id).orElseThrow(()->new RuntimeException("Championship not found"));
    }

    public List<StandingResponse> getTable(Long championshipId){
        List<Match> matches = matchRepository.findByChampionshipId(championshipId);
        if (matches == null){
            throw new RuntimeException("Matches not found");
        }

        Map<Long, StandingResponse> table = new HashMap<>();

        for (Match match : matches){
            if(match.getHomeScore() == null || match.getAwayScore() == null){
                continue;
            }

            Team home = match.getHomeTeam();
            Team away = match.getAwayTeam();

            table.putIfAbsent(home.getId(), createStanding(home));
            table.putIfAbsent(away.getId(), createStanding(away));

            StandingResponse homeStats = table.get(home.getId());
            StandingResponse awayStats = table.get(away.getId());

            homeStats.setMatchesPlayed(homeStats.getMatchesPlayed()+1);
            awayStats.setMatchesPlayed(awayStats.getMatchesPlayed()+1);

            Integer homeGoals = match.getHomeScore();
            Integer awayGoals = match.getAwayScore();

            homeStats.setGoalsFor(homeStats.getGoalsFor() + homeGoals);
            homeStats.setGoalsAgainst(homeStats.getGoalsAgainst() + awayGoals);
            homeStats.setGoalsDifference(homeStats.getGoalsFor() - homeStats.getGoalsAgainst());
            awayStats.setGoalsFor(awayStats.getGoalsFor() + awayGoals);
            awayStats.setGoalsAgainst(awayStats.getGoalsAgainst() + homeGoals);
            awayStats.setGoalsDifference(awayStats.getGoalsFor() - awayStats.getGoalsAgainst());

            if (homeGoals > awayGoals){
                homeStats.setWon(homeStats.getWon()+1);
                homeStats.setPoints(homeStats.getPoints()+3);
                awayStats.setLost(awayStats.getLost()+1);
            } else if (homeGoals < awayGoals){
                awayStats.setWon(awayStats.getWon()+1);
                awayStats.setPoints(awayStats.getPoints()+3);
                homeStats.setLost(homeStats.getLost()+1);
            } else{
                homeStats.setDrawn(homeStats.getDrawn()+1);
                homeStats.setPoints(homeStats.getPoints()+1);
                awayStats.setDrawn(awayStats.getDrawn()+1);
                awayStats.setPoints(awayStats.getPoints()+1);
            }
        }

        List<StandingResponse> standings = sortStandings(table, matches);

        for (int i = 0; i < standings.size(); i++) {
            standings.get(i).setPosition(i + 1);
        }

        return standings;
    }

    private StandingResponse createStanding(Team team) {
        StandingResponse standing = new StandingResponse();
        standing.setTeamId(team.getId());
        standing.setTeamName(team.getName());

        return standing;
    }

    private List<StandingResponse> sortStandings(Map<Long, StandingResponse> table, List<Match> matches) {
        List<StandingResponse> standings = new ArrayList<>(table.values());

        standings.sort((a, b) -> {
            if (!b.getPoints().equals(a.getPoints())){
                return b.getPoints() - a.getPoints();
            }
            if (!b.getWon().equals(a.getWon())){
                return b.getWon() - a.getWon();
            }
            if (!b.getGoalsDifference().equals(a.getGoalsDifference())){
                return b.getGoalsDifference() - a.getGoalsDifference();
            }
            if (!b.getGoalsFor().equals(a.getGoalsFor())){
                return b.getGoalsFor() - a.getGoalsFor();
            }

            return headToHead(a, b, matches);
        });
        return standings;
    }

    private int headToHead(StandingResponse a, StandingResponse b, List<Match> matches) {
        int goalsA = 0;
        int goalsB = 0;

        for (Match match : matches){
            if(match.getHomeScore() == null || match.getAwayScore() == null){ continue; }

            Long homeId = match.getHomeTeam().getId();
            Long awayId = match.getAwayTeam().getId();

            boolean isDirectMatch = (homeId.equals(a.getTeamId()) && awayId.equals(b.getTeamId())) || (awayId.equals(a.getTeamId()) && homeId.equals(b.getTeamId()));

            if(!isDirectMatch){ continue; }

            if(homeId.equals(a.getTeamId())){
                goalsA += match.getHomeScore();
                goalsB += match.getAwayScore();
            } else {
                goalsB += match.getHomeScore();
                goalsA += match.getAwayScore();
            }
        }
        return goalsB - goalsA;
    }
}

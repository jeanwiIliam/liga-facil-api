package com.jeanwilliam.ligafacil.service;

import com.jeanwilliam.ligafacil.dto.response.MatchResponse;
import com.jeanwilliam.ligafacil.entity.Championship;
import com.jeanwilliam.ligafacil.entity.Match;
import com.jeanwilliam.ligafacil.entity.Team;
import com.jeanwilliam.ligafacil.repository.ChampionshipRepository;
import com.jeanwilliam.ligafacil.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final ChampionshipRepository championshipRepository;

    public List<MatchResponse> generateMatches(Long championshipId){
        Championship championship = championshipRepository.findById(championshipId).orElseThrow(() -> new RuntimeException("Championship not found"));

        List<Team> teams = championship.getTeams();
        if (teams.size() % 2 != 0){
            throw new RuntimeException("Number of teams must be even");
        }

        int numberOfTeams = teams.size();
        int totalRounds = numberOfTeams - 1;
        int matchesPerRound = numberOfTeams / 2;

        List<Team> rotation = new ArrayList<>(teams);
        Collections.shuffle(rotation);

        List<MatchResponse> createdMatches = new ArrayList<>();

        for (int round = 0; round < totalRounds; round++){
            for (int match = 0; match < matchesPerRound; match++){
                Team team1 = rotation.get(match);
                Team team2 = rotation.get(numberOfTeams - 1 - match);

                Team home;
                Team away;

                if ((round+match) % 2 == 0){
                    home = team1;
                    away = team2;
                }
                else {
                    home = team2;
                    away = team1;
                }

                Match newMatch = new Match();
                newMatch.setChampionship(championship);
                newMatch.setRound(round + 1);
                newMatch.setHomeTeam(home);
                newMatch.setAwayTeam(away);

                matchRepository.save(newMatch);

                createdMatches.add(new MatchResponse(
                        newMatch.getId(),
                        newMatch.getRound(),
                        home.getName(),
                        away.getName(),
                        newMatch.getHomeScore(),
                        newMatch.getAwayScore()
                ));
            }
            Team last = rotation.remove(rotation.size()-1);
            rotation.add(1, last);
        }
        return createdMatches;
    }

    public List<Match> getMatches(Long championshipId, Integer round){
        if (round == null){
            return matchRepository.findByChampionshipId(championshipId);
        }
        return matchRepository.findByChampionshipIdAndRound(championshipId, round);
    }

    public Match findById(Long id) {
        return matchRepository.findById(id).orElseThrow(() -> new RuntimeException("Match not found"));
    }

    public Match updateScore(Long id, Integer homeScore, Integer awayScore) {
        Match match = matchRepository.findById(id).orElseThrow(() -> new RuntimeException("Match not found"));
        if (homeScore < 0 || awayScore < 0){
            throw new RuntimeException("Score cannot be negative");
        }
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        return matchRepository.save(match);
    }
}

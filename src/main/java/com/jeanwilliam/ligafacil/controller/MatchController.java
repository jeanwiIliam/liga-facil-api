package com.jeanwilliam.ligafacil.controller;

import com.jeanwilliam.ligafacil.dto.request.MatchScoreRequest;
import com.jeanwilliam.ligafacil.dto.response.MatchResponse;
import com.jeanwilliam.ligafacil.entity.Match;
import com.jeanwilliam.ligafacil.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;

    @PostMapping("/championships/{id}/generate-matches")
    public ResponseEntity<List<MatchResponse>> generateMatches(@PathVariable Long id){
        List<MatchResponse> matches = matchService.generateMatches(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(matches);
    }

    @GetMapping("/championships/{id}/matches")
    public List<MatchResponse> getMatches(@PathVariable Long id, @RequestParam(required = false) Integer round){
        return matchService.getMatches(id, round)
                .stream()
                .map(match -> new MatchResponse(match))
                .toList();
    }

    @GetMapping("/matches/{id}")
    public MatchResponse getMatchById(@PathVariable Long id){
        Match match = matchService.findById(id);
        return new MatchResponse(match);
    }

    @PatchMapping("/matches/{id}")
    public MatchResponse updateScore(@PathVariable Long id, @RequestBody MatchScoreRequest request){
        Match match = matchService.updateScore(id, request.getHomeScore(), request.getAwayScore());
        return new MatchResponse(match);
    }
}

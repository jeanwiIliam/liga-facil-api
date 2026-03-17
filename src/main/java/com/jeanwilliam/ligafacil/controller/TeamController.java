package com.jeanwilliam.ligafacil.controller;

import com.jeanwilliam.ligafacil.dto.request.TeamRequest;
import com.jeanwilliam.ligafacil.dto.response.TeamResponse;
import com.jeanwilliam.ligafacil.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping(value ="/championships/{id}/teams", consumes = "multipart/form-data")
    public ResponseEntity<TeamResponse> createTeam(@PathVariable Long id, @RequestPart TeamRequest teamRequest, @RequestPart(required = false) MultipartFile image){
        TeamResponse teamResponse = teamService.create(id, teamRequest, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamResponse);
    }

    @GetMapping("/championships/{id}/teams")
    public List<TeamResponse> getTeamsByChampionship(@PathVariable Long id){
        return teamService.findByChampionship(id)
                .stream()
                .map(team -> new TeamResponse(team))
                .toList();
    }
    @PutMapping(value = "/teams/{id}", consumes = "multipart/form-data")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long id,
            @RequestPart TeamRequest teamRequest,
            @RequestPart(required = false) MultipartFile image
    ) {
        TeamResponse updated = teamService.update(id, teamRequest, image);
        return ResponseEntity.ok(updated);
    }
}

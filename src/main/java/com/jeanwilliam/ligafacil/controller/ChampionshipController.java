package com.jeanwilliam.ligafacil.controller;

import com.jeanwilliam.ligafacil.dto.request.ChampionshipRequest;
import com.jeanwilliam.ligafacil.dto.response.ChampionshipResponse;
import com.jeanwilliam.ligafacil.dto.response.StandingResponse;
import com.jeanwilliam.ligafacil.entity.Championship;
import com.jeanwilliam.ligafacil.service.ChampionshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("championships")
@RequiredArgsConstructor
public class ChampionshipController {
    private final ChampionshipService championshipService;

    @PostMapping
    public ResponseEntity<ChampionshipResponse> createChampionship(@RequestBody ChampionshipRequest championshipRequest){
        ChampionshipResponse response = championshipService.create(championshipRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<ChampionshipResponse> getChampionships(){
        return championshipService.findAll()
                .stream()
                .map(championship -> new ChampionshipResponse(championship))
                .toList();
    }

    @GetMapping("/{id}")
    public ChampionshipResponse getChampionshipById(@PathVariable Long id){
        Championship championship = championshipService.findById(id);
        return new ChampionshipResponse(championship);
    }

    @GetMapping("/{id}/standings")
    public List<StandingResponse> getStandings(@PathVariable Long id){
        return championshipService.getTable(id);
    }
}

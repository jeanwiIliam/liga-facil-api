package com.jeanwilliam.ligafacil.service;

import com.jeanwilliam.ligafacil.dto.request.TeamRequest;
import com.jeanwilliam.ligafacil.dto.response.TeamResponse;
import com.jeanwilliam.ligafacil.entity.Championship;
import com.jeanwilliam.ligafacil.entity.Team;
import com.jeanwilliam.ligafacil.repository.ChampionshipRepository;
import com.jeanwilliam.ligafacil.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final ChampionshipRepository championshipRepository;

    public TeamResponse create(Long championshipId, TeamRequest request, MultipartFile image) {
        Championship championship = championshipRepository.findById(championshipId).orElseThrow(() -> new RuntimeException("Championship not found"));

        Team team = new Team(request);
        team.setChampionship(championship);

        if (image != null && !image.isEmpty()) {

            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

            try {
                Path uploadPath = Paths.get("uploads");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, image.getBytes());
                team.setImage("uploads/" + fileName);

            } catch (IOException e) {
                throw new RuntimeException("Error saving image", e);
            }
        }

        teamRepository.save(team);
        return new TeamResponse(team);
    }

    public List<Team> findByChampionship(Long id){
        return teamRepository.findByChampionshipIdOrderByNameAsc(id);
    }

    public TeamResponse update(Long teamId, TeamRequest request, MultipartFile image) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        team.setName(request.name());

        if (image != null && !image.isEmpty()) {

            if (team.getImage() != null) {
                try {
                    Path oldImagePath = Paths.get(team.getImage());
                    Files.deleteIfExists(oldImagePath);
                } catch (IOException e) {
                    throw new RuntimeException("Error deleting old image", e);
                }
            }

            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

            try {
                Path uploadPath = Paths.get("uploads");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, image.getBytes());
                team.setImage("uploads/" + fileName);

            } catch (IOException e) {
                throw new RuntimeException("Error saving image", e);
            }
        }

        teamRepository.save(team);
        return new TeamResponse(team);
    }
}

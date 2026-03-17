package com.jeanwilliam.ligafacil.entity;

import com.jeanwilliam.ligafacil.dto.request.ChampionshipRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "championships")
public class Championship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "championship")
    private List<Team> teams;

    @OneToMany(mappedBy = "championship")
    private List<Match> matches;

    public Championship(ChampionshipRequest championshipRequest) {
        this.name = championshipRequest.name();
    }
}

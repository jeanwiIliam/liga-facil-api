package com.jeanwilliam.ligafacil.entity;

import com.jeanwilliam.ligafacil.dto.request.TeamRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String image;

    @ManyToOne
    @JoinColumn(name = "championship_id")
    private Championship championship;

    public Team(TeamRequest request) {
        this.name = request.name();
    }
}

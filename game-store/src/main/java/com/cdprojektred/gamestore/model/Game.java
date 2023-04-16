package com.cdprojektred.gamestore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @Min(value = 0)
    private int cost;

    public Game(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }
}
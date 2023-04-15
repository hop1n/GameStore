package com.cdprojektred.gamestore.dto;

import com.cdprojektred.gamestore.model.Game;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameDto {

    private String name;
    private int cost;

    public Game toGame(){
        return new Game(name, cost);
    }

}

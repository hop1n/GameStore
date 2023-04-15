package com.cdprojektred.gamestore.api;

import com.cdprojektred.gamestore.dto.GameDto;
import com.cdprojektred.gamestore.model.Game;
import com.cdprojektred.gamestore.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/search")
    public List<Game> search(@RequestParam String gameName) {
        return gameService.searchGame(gameName);
    }

    @RequestMapping("/browse")
    public List<Game> browseCatalog() {
        return gameService.getAll();
    }

    @PostMapping("/add")
    public Game addGame(@RequestBody GameDto gameDto) {
        return gameService.addGame(gameDto.toGame());
    }

    @PostMapping("/delete/{gameId}")
    public String deleteGame(@PathVariable long gameId) {
        gameService.deleteGameById(gameId);
        return "Game has been deleted";
    }

    @PostMapping("/update")
    public String updateGame(@RequestBody Game game) {
        gameService.updateGame(game);
        return "Game has been updated";
    }
}

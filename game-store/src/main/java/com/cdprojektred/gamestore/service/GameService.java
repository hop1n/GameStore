package com.cdprojektred.gamestore.service;

import com.cdprojektred.gamestore.exceptions.GameNotFoundException;
import com.cdprojektred.gamestore.model.Game;
import com.cdprojektred.gamestore.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    public static final String NO_SUCH_GAME_MESSAGE = "There is no game with id: %d";

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getAll() {
        return gameRepository.findAll();
    }

    public Game addGame(String name, int cost) {
        return gameRepository.save(new Game(name, cost));
    }

    public List<Game> searchGame(String name) {
        return gameRepository.findGameByNameContaining(name);
    }

    private void findById(long id) {
        gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException(NO_SUCH_GAME_MESSAGE.formatted(id)));
    }

    public void deleteGameById(Long id) {
        findById(id);
        gameRepository.deleteById(id);
    }

    public Game updateGame(Game game) {
        findById(game.getId());
        return gameRepository.save(game);
    }
}
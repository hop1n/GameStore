package com.cdprojektred.gamestore.service;

import com.cdprojektred.gamestore.dto.GameDto;
import com.cdprojektred.gamestore.exceptions.GameNotFoundException;
import com.cdprojektred.gamestore.model.Game;
import com.cdprojektred.gamestore.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceTest {

    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(gameRepository);
    }

    @Test
    public void testGetAll() {
        List<Game> expectedGames = new ArrayList<>();
        expectedGames.add(new Game("Game 1", 10));
        expectedGames.add(new Game("Game 2", 20));
        when(gameRepository.findAll()).thenReturn(expectedGames);

        List<Game> actualGames = gameService.getAll();

        verify(gameRepository, times(1)).findAll();
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void testAddGame() {
        GameDto gameDto = new GameDto("Game 1", 10);
        Game expectedGame = new Game("Game 1", 10);
        when(gameRepository.save(any(Game.class))).thenReturn(expectedGame);

        Game actualGame = gameService.addGame(gameDto.getName(), gameDto.getCost());

        verify(gameRepository, times(1)).save(any(Game.class));
        assertEquals(expectedGame, actualGame);
    }

    @Test
    public void testSearchGame() {
        List<Game> expectedGames = new ArrayList<>();
        expectedGames.add(new Game("Game 1", 10));
        when(gameRepository.findGameByNameContaining("Game")).thenReturn(expectedGames);

        List<Game> actualGames = gameService.searchGame("Game");

        verify(gameRepository, times(1)).findGameByNameContaining("Game");
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void testDeleteGameById() {
        Long gameId = 1L;
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(new Game("Game 1", 10)));

        gameService.deleteGameById(gameId);

        verify(gameRepository, times(1)).findById(gameId);
        verify(gameRepository, times(1)).deleteById(gameId);
    }

    @Test
    public void testDeleteGameByIdThrowsExceptionWhenGameNotFound() {
        Long gameId = 1L;
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.deleteGameById(gameId));
    }

    @Test
    public void testUpdateGame() {
        Game game = new Game(1L,"Game 1", 10);
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(gameRepository.save(game)).thenReturn(game);

        gameService.updateGame(game);

        verify(gameRepository, times(1)).findById(game.getId());
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testUpdateGameThrowsExceptionWhenGameNotFound() {
        Game game = new Game(1L,"Game 1", 10);
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.updateGame(game));
    }
}
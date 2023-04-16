package com.cdprojektred.gamestore.api;

import com.cdprojektred.gamestore.dto.GameDto;
import com.cdprojektred.gamestore.model.Game;
import com.cdprojektred.gamestore.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {

    @Mock
    private GameService gameService;

    private GameController gameController;

    @BeforeEach
    void init() {
        gameController = new GameController(gameService);
    }

    @Test
    public void searchGameTest() {
        String gameName = "Cyberpunk 2077";
        List<Game> expectedGames = new ArrayList<>();
        expectedGames.add(new Game("Cyberpunk 2077", 49));
        when(gameService.searchGame(gameName)).thenReturn(expectedGames);

        List<Game> actualGames = gameController.search(gameName);

        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void browseTest() {
        List<Game> expectedGames = new ArrayList<>();
        expectedGames.add(new Game("Cyberpunk 2077", 49));
        when(gameService.getAll()).thenReturn(expectedGames);

        List<Game> actualGames = gameController.browseCatalog();

        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void addGameTest() {
        GameDto gameDto = new GameDto("Cyberpunk 2077", 49);
        Game expectedGame = new Game("Cyberpunk 2077", 49);

        when(gameService.addGame(gameDto.getName(), gameDto.getCost())).thenReturn(expectedGame);

        assertEquals(expectedGame, gameController.addGame(gameDto));
    }

    @Test
    public void deleteGameTest() {
        long gameId = 1L;

        gameController.deleteGame(gameId);

        verify(gameService, times(1)).deleteGameById(gameId);
    }

    @Test
    public void updateGameInfoTest() {
        Game game = new Game("Cyberpunk 2077", 59);

        when(gameService.updateGame(game)).thenReturn(game);

        assertEquals(game, gameController.updateGame(game));
    }
}
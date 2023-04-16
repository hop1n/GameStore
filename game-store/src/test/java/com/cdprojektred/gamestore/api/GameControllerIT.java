package com.cdprojektred.gamestore.api;

import com.cdprojektred.gamestore.dto.AuthDto;
import com.cdprojektred.gamestore.dto.LoginDto;
import com.cdprojektred.gamestore.model.Game;
import com.cdprojektred.gamestore.repository.GameRepository;
import com.cdprojektred.gamestore.security.JWTAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GameControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @Autowired
    private GameRepository repository;

    private List<Game> games;

    @BeforeEach
    void beforeMethod() throws Exception {
        games = repository.saveAll(Arrays.asList(
                new Game("The Witcher 1", 10),
                new Game("The Witcher 2", 20),
                new Game("The Witcher 3", 30))
        );
        String jsonResponseContent = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDto("admin", "admin"))))
                .andReturn().getResponse().getContentAsString();
        token = objectMapper.readValue(jsonResponseContent, AuthDto.class).getAccessToken();
    }

    @Test
    public void testBrowse() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/games/browse"));

        resultActions.andExpect(status().isOk());
        for (int i = 0; i < games.size(); i++) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[%d].id".formatted(i)).value(games.get(i).getId()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[%d].name".formatted(i)).value(games.get(i).getName()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[%d].cost".formatted(i)).value(games.get(i).getCost()));
        }
    }

    @Test
    public void testSearch() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/games/search?gameName=The"));

        resultActions.andExpect(status().isOk());
        for (int j = 0; j < games.size(); j++) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[%d].id".formatted(j)).value(games.get(j).getId()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[%d].name".formatted(j)).value(games.get(j).getName()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[%d].cost".formatted(j)).value(games.get(j).getCost()));
        }
    }

    @Test
    public void testSearchNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/games/search?gameName=BadSearch"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void addTest() throws Exception {
        Game game = new Game("The Witcher 4", 40);

        String requestJson = objectMapper.writeValueAsString(game);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/add")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(game.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cost").value(game.getCost()));
    }

    @Test
    public void addBadNameTest() throws Exception {
        Game game = new Game("", 40);

        String requestJson = objectMapper.writeValueAsString(game);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/add")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addBadCostTest() throws Exception {
        Game game = new Game("Test", -10);

        String requestJson = objectMapper.writeValueAsString(game);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/add")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addTestBadToken() throws Exception {
        Game game = new Game("The Witcher 4", 40);

        String requestJson = objectMapper.writeValueAsString(game);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/add")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + "bad token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteGameBadTokenTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/delete/%d".formatted(games.get(0).getId()))
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + "bad token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteGameTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/delete/%d".formatted(games.get(0).getId()))
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + token))
                .andExpect(status().isOk());
    }

    @Test
    public void updateGame() throws Exception {
        Game gameToUpdate = games.get(0);
        gameToUpdate.setName("newName");

        String requestJson = objectMapper.writeValueAsString(gameToUpdate);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/update")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + token))
                .andExpect(status().isOk());
    }

    @Test
    public void updateGameBadTokenTest() throws Exception {
        Game gameToUpdate = games.get(0);

        String requestJson = objectMapper.writeValueAsString(gameToUpdate);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/update")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + "bad token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateGameBadGameInfoTest() throws Exception {
        Game gameToUpdate = new Game(12L, "Cyberpunk 2077", 20);

        String requestJson = objectMapper.writeValueAsString(gameToUpdate);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/update")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateGameBadNameTest() throws Exception {
        Game gameToUpdate = games.get(0);
        gameToUpdate.setName("");

        String requestJson = objectMapper.writeValueAsString(gameToUpdate);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/update")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateGameBadCostTest() throws Exception {
        Game gameToUpdate = games.get(0);
        gameToUpdate.setCost(-1);

        String requestJson = objectMapper.writeValueAsString(gameToUpdate);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/update")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JWTAuthenticationFilter.BEARER_HEADER_PREFIX + token))
                .andExpect(status().isBadRequest());
    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll(repository.findAll());
    }
}

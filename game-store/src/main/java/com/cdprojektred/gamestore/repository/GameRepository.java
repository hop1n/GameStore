package com.cdprojektred.gamestore.repository;

import com.cdprojektred.gamestore.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findGameByNameContaining(String name);
}
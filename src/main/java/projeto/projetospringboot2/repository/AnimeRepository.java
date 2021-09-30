package projeto.projetospringboot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.projetospringboot2.domain.Anime;

import java.util.List;

public interface AnimeRepository extends JpaRepository <Anime, Long> {
    List<Anime> findByName(String name);
}

package projeto.projetospringboot2.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import projeto.projetospringboot2.domain.Anime;
import projeto.projetospringboot2.util.AnimeCreator;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {
    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("Save persists anime when successful")
    void save_PersistsAnime_WhenSuccessful() {
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(anime);

        Assertions.assertThat(animeSaved).isNotNull();
        Assertions.assertThat(animeSaved.getId()).isNotNull();
        Assertions.assertThat(animeSaved.getName()).isEqualTo(anime.getName());
    }

    @Test
    @DisplayName("Save updates anime when successful")
    void save_UpdatesAnime_WhenSuccessful() {
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(anime);
        animeSaved.setName("Overlord");
        Anime animeUpdaTed = this.animeRepository.save(animeSaved);

        Assertions.assertThat(animeUpdaTed).isNotNull();
        Assertions.assertThat(animeUpdaTed.getId()).isNotNull();
        Assertions.assertThat(animeUpdaTed.getName()).isEqualTo(animeSaved.getName());
    }

    @Test
    @DisplayName("Delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful() {
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(anime);

        this.animeRepository.delete(animeSaved);

        Optional<Anime> animeOptional = this.animeRepository.findById(animeSaved.getId());

        Assertions.assertThat(animeOptional).isEmpty();
    }

    @Test
    @DisplayName("Find by name returns list of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {

        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(anime);
        String name = animeSaved.getName();

        List<Anime> animes = this.animeRepository.findByName(name);


        Assertions.assertThat(animes)
                .isNotEmpty()
                .contains(animeSaved);

    }

    @Test
    @DisplayName("Find by name returns empty list of anime when no anime is found")
    void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {

        List<Anime> animes = this.animeRepository.findByName("123456");

        Assertions.assertThat(animes).isEmpty();
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is null")
    void save_ThrowConstraintViolationException_WhenNameIsEmpty() {
        Anime anime = new Anime();
//       Assertions.assertThatThrownBy(() -> this.animeRepository.save(anime))
//               .isInstanceOf(ConstraintViolationException.class);

        Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> this.animeRepository.save(anime));

    }

}
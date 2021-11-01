package projeto.projetospringboot2.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import projeto.projetospringboot2.domain.Anime;
import projeto.projetospringboot2.repository.AnimeRepository;
import projeto.projetospringboot2.requests.AnimePostRequestBody;
import projeto.projetospringboot2.util.AnimeCreator;
import projeto.projetospringboot2.util.AnimePostRequestBodyCreator;
import projeto.projetospringboot2.wrapper.PageableResponse;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class AnimeControllerIT {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @LocalServerPort
    private int port;
    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("List returns list of anime inside page object when successful.")
    void list_ReturnListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplate.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage.toList()).isNotNull();

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("List returns list of anime when successful.")
    void list_ReturnListOfAnimes_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        List<Anime> animes = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull();

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findById returns anime when successful.")
    void findById_ReturnAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        Long expectedId = savedAnime.getId();
        Anime  anime = testRestTemplate.getForObject("/animes/{id}",Anime.class,expectedId);
        Assertions.assertThat(anime)
                .isNotNull();

        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByName returns a List anime when successful.")
    void findByName_ReturnAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> anime = testRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(anime.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns empty List of anime when anime is not found.")
    void findByName_ReturnEmptyList_WhenAnimeIsNotFound() {

        List<Anime> anime = testRestTemplate.exchange("/animes/find?name=aaa", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful.")
    void save_CreateAnime_WhenSuccessful() {
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
        ResponseEntity<Anime> animeResponseEntity = testRestTemplate.postForEntity("/animes",animePostRequestBody,Anime.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("replace returns anime when successful.")
    void replace_UpdateAnime_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        savedAnime.setName("New Name");
        ResponseEntity<Void> animeResponseEntity = testRestTemplate.exchange("/animes",
                HttpMethod.PUT, new HttpEntity<>(savedAnime),Void.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> animeResponseEntity = testRestTemplate.exchange("/animes/{id}",
                HttpMethod.DELETE,null, Void.class, savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}

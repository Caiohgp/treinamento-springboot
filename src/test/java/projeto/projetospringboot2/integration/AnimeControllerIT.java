package projeto.projetospringboot2.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import projeto.projetospringboot2.domain.Anime;
import projeto.projetospringboot2.domain.ProjectUser;
import projeto.projetospringboot2.repository.AnimeRepository;
import projeto.projetospringboot2.repository.ProjectUserRepository;
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
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;
    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;
    @Autowired
    private ProjectUserRepository projectUserRepository;
    @Autowired
    private AnimeRepository animeRepository;
    public static final ProjectUser USER = ProjectUser.builder()
            .name("Caio Henrique")
            .password("{bcrypt}$2a$10$1.qo7jDXJ4TZwshVBY1SGuz12tz6Gdg1wBS1Mb3ZRL1pbwve9OiBO")
            .username("caio")
            .authorities("ROLE_USER")
            .build();

    public static final ProjectUser ADMIN = ProjectUser.builder()
            .name("Caio Henrique")
            .password("{bcrypt}$2a$10$1.qo7jDXJ4TZwshVBY1SGuz12tz6Gdg1wBS1Mb3ZRL1pbwve9OiBO")
            .username("caiohgp")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Config{
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}")int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("caio","caio2304");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}")int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("caiohgp","caio2304");
            return new TestRestTemplate(restTemplateBuilder);
        }

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful.")
    void list_ReturnListOfAnimesInsidePageObject_WhenSuccessful() {

        projectUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage.toList()).isNotNull();

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("List returns list of anime when successful.")
    void list_ReturnListOfAnimes_WhenSuccessful() {

        projectUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull();

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findById returns anime when successful.")
    void findById_ReturnAnime_WhenSuccessful() {

        projectUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        Long expectedId = savedAnime.getId();
        Anime  anime = testRestTemplateRoleUser.getForObject("/animes/{id}",Anime.class,expectedId);
        Assertions.assertThat(anime)
                .isNotNull();

        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByName returns a List anime when successful.")
    void findByName_ReturnAnime_WhenSuccessful() {

        projectUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> anime = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
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

        projectUserRepository.save(USER);

        List<Anime> anime = testRestTemplateRoleUser.exchange("/animes/find?name=aaa", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful.")
    void save_CreateAnime_WhenSuccessful() {

        projectUserRepository.save(USER);

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
        ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes",animePostRequestBody,Anime.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("replace returns anime when successful.")
    void replace_UpdateAnime_WhenSuccessful() {

        projectUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        savedAnime.setName("New Name");
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes",
                HttpMethod.PUT, new HttpEntity<>(savedAnime),Void.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful(){

        projectUserRepository.save(ADMIN);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}",
                HttpMethod.DELETE,null, Void.class, savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns error 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin(){

        projectUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}",
                HttpMethod.DELETE,null, Void.class, savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}

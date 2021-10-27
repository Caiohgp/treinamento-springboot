package projeto.projetospringboot2.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import projeto.projetospringboot2.controller.AnimeController;
import projeto.projetospringboot2.domain.Anime;
import projeto.projetospringboot2.exception.BadRequestException;
import projeto.projetospringboot2.repository.AnimeRepository;
import projeto.projetospringboot2.requests.AnimePostRequestBody;
import projeto.projetospringboot2.requests.AnimePutRequestBody;
import projeto.projetospringboot2.util.AnimeCreator;
import projeto.projetospringboot2.util.AnimePostRequestBodyCreator;
import projeto.projetospringboot2.util.AnimePutRequestBodyCreator;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {
    @InjectMocks
    private AnimeService animeService;
    @Mock
    private AnimeRepository animeRepositoryMock;

    @BeforeEach
    void setup() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of((AnimeCreator.createValidAnime())));
        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("List All returns list of anime inside page object when successful.")
    void listAll_ReturnListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeService.listAll(PageRequest.of(1,1));
        Assertions.assertThat(animePage.toList()).isNotNull();
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("List all non pageable returns list of anime when successful.")
    void listAllNonPageable_ReturnListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.listAllNonPageable();
        Assertions.assertThat(animeList)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
        ;

        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException returns anime when successful.")
    void findByIdOrThrowBadRequestException_ReturnAnime_WhenSuccessful() {
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime  anime = animeService.findByIdOrThrowBadRequestException(1);
        Assertions.assertThat(anime)
                .isNotNull();

        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException throws BadRequestException when anime is not found.")
    void findByIdOrThrowBadRequestException_ThrowsBadRequestException_WhenAnimeNotFound() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> animeService.findByIdOrThrowBadRequestException(1));

    }

    @Test
    @DisplayName("findByName returns a List anime when successful.")
    void findByName_ReturnAnime_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> anime = animeService.findByName("Bleach");
        Assertions.assertThat(anime)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(anime.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns empty List of anime when anime is not found.")
    void findByName_ReturnEmptyList_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> anime = animeService.findByName("Bleach");
        Assertions.assertThat(anime)
                .isNotNull()
                .isEmpty();
    }


    @Test
    @DisplayName("save returns anime when successful.")
    void save_CreateAnime_WhenSuccessful() {

        Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());
        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());

    }

    @Test
    @DisplayName("replace returns anime when successful.")
    void replace_UpdateAnime_WhenSuccessful() {

        Assertions.assertThatCode(()-> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                .doesNotThrowAnyException();

    }

    @Test
    @DisplayName("delete returns anime when successful.")
    void delete_DeleteAnime_WhenSuccessful() {

        Assertions.assertThatCode(()-> animeService.delete(1))
                .doesNotThrowAnyException();

    }
}
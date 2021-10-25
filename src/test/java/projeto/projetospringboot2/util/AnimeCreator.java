package projeto.projetospringboot2.util;

import projeto.projetospringboot2.domain.Anime;

public class AnimeCreator {
    public static Anime createAnimeToBeSaved(){
        return Anime.builder()
                .name("Bleach")
                .build();
    }

    public static Anime createValidAnime(){
        return Anime.builder()
                .name("Bleach")
                .id(1L)
                .build();
    }

    public static Anime createValidUpdatedAnime(){
        return Anime.builder()
                .name("Bleach")
                .id(1L)
                .build();
    }
}

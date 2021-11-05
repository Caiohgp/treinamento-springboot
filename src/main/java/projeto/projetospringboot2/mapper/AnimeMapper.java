package projeto.projetospringboot2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import projeto.projetospringboot2.domain.Anime;
import projeto.projetospringboot2.requests.AnimePostRequestBody;
import projeto.projetospringboot2.requests.AnimePutRequestBody;

@Mapper(componentModel = "spring")
public abstract class AnimeMapper {
    public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

    public abstract Anime toAnime(AnimePostRequestBody animePostRequestBody);

    public abstract Anime toAnime(AnimePutRequestBody animePosttRequestBody);
}

package projeto.projetospringboot2.requests;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;

@Data
public class AnimePostRequestBody {
    @NotEmpty(message="The anime cannot be empty")
    private String name;
    @URL(message = "The URL is not valid")
    private String url;
}

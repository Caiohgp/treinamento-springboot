package projeto.projetospringboot2.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;

@Getter
@SuperBuilder
public class ValidationExceptionDetails extends ExceptionDetails{
    private final String fields;
    private final String fieldsMessage;
}

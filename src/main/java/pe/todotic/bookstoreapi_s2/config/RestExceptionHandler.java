package pe.todotic.bookstoreapi_s2.config;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.todotic.bookstoreapi_s2.exception.MediaFileNotFoundException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Esta clase sirve para configurar los mensajes del properties, solo sirven por default en MVC

@RestControllerAdvice
public class RestExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidationError (MethodArgumentNotValidException validException) { // Recibirlo como parametro es opcional el importante es el de la anotacion
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetail.setTitle("Unprocessable entity");
        problemDetail.setType(URI.create("https://api.bookstore.com/errors/unprocessable-entity"));
        problemDetail.setDetail("The entity can not processed because it has errors");

        List<FieldError> fieldErros = validException.getFieldErrors();
        List<String> errors = new ArrayList<>();

        for (FieldError fe : fieldErros) {
            String message = messageSource.getMessage(fe, Locale.getDefault());
            errors.add(message);
        }

        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    // Se pueden devolver varias excepciones juntas usando {} como Entity y Media o bien usado la anotacion en la clase
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class, /*MediaFileNotFoundException.class*/})
    void handleEntityNotFoundException (EntityNotFoundException exception) {

    }

}

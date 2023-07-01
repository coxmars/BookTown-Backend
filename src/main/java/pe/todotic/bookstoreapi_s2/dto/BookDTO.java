package pe.todotic.bookstoreapi_s2.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookDTO {
    @NotNull
    @Size(min = 3, max = 100)
    private String title;
    @NotNull
    @Min(0)
    private Float price;
    @NotNull
    @Pattern(regexp = "[a-z0-9-]+")
    private String slug;
    @NotBlank
    private String coverPath;
    @NotBlank
    private String filePath;
    @NotBlank(message = "La descripción es obligatoria")
    private String desc;
}

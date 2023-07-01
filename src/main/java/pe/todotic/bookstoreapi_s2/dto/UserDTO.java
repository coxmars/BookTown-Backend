package pe.todotic.bookstoreapi_s2.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import pe.todotic.bookstoreapi_s2.model.Role;

@Data
public class UserDTO {
    @NotBlank
    @Size(max = 45)
    private String firstName;

    @NotBlank
    @Size(max = 45)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 45)
    private String email;

    @NotEmpty
    private String password;

    @NotNull
    private Role role;
}

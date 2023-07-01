package pe.todotic.bookstoreapi_s2.controller;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.todotic.bookstoreapi_s2.model.User;
import pe.todotic.bookstoreapi_s2.repository.IUserRepository;
import pe.todotic.bookstoreapi_s2.dto.UserDTO;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private IUserRepository userRepository;

    /**
     * Devuelve la lista de usuarios de forma paginada.
     * El cliente puede enviar los parámetros page, size, sort,... en la URL
     * para configurar la página solicitada.
     * Si el cliente no envía ningún parámetro para la paginación,
     * se toma la configuración por defecto.
     * Retorna el status OK: 200
     * Ej.: GET http://localhost:9090/api/admin/users?page=0&size=2&sort=nombreCompleto,desc
     *
     * @param pageable la configuración de paginación que captura los parámetros como: page, size y sort
     */
    @GetMapping
    Page<User> paginate(@PageableDefault(sort = "fullName", size = 10) Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Devuelve la lista completa de usuarios
     * Retorna el status OK: 200
     * Ej.: GET http://localhost:9090/api/admin/users
     */
    @GetMapping("/listar")
    List<User> list() {
        return userRepository.findAll();
    }

    /**
     * Devuelve un usuario por su ID.
     * Retorna el status OK: 200
     * Ej.: GET http://localhost:9090/api/admin/users/1
     */
    @GetMapping("/{id}")
    User get(@PathVariable Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Crea un usuario a partir del cuerpo
     * de la solicitud HTTP y retorna
     * el usuario creado.
     * Retorna el status CREATED: 201
     * Ej.: POST http://localhost:9090/api/admin/users
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    User create(@Validated @RequestBody UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        userRepository.save(user);
        return user;
    }

    /**
     * Actualiza un usuario por su ID, a partir
     * del cuerpo de la solicitud HTTP.
     * Retorna el status OK: 200.
     * Ej.: PUT http://localhost:9090/api/admin/users/1
     */
    @PutMapping("/{id}")
    User update(
            @PathVariable Integer id,
            @Validated @RequestBody UserDTO userDTO
    ) {
        User user = userRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);

        modelMapper.map(userDTO, user);

        return user;
    }

    /**
     * Elimina un usuario por su ID.
     * Retorna el status NO_CONTENT: 204
     * Ej.: DELETE http://localhost:9090/api/admin/users/1
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);

        userRepository.delete(user);
    }

}

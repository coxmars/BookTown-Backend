package pe.todotic.bookstoreapi_s2.controller;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.todotic.bookstoreapi_s2.dto.SignupUserDTO;
import pe.todotic.bookstoreapi_s2.exception.BadRequestException;
import pe.todotic.bookstoreapi_s2.model.Role;
import pe.todotic.bookstoreapi_s2.model.User;
import pe.todotic.bookstoreapi_s2.repository.IUserRepository;

import java.security.Principal;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    User getMyInformation (Principal principal) {
        return userRepository.findOneByEmail(principal.getName())
                .orElseThrow(EntityNotFoundException::new);
    }

    @PostMapping("/signup")
    User signup (@RequestBody @Validated SignupUserDTO userDTO) {
        boolean emailAlreadyExist = userRepository.existsByEmail(userDTO.getEmail());

        if (emailAlreadyExist) {
            throw new BadRequestException("Email already exists");
        }

        String password = passwordEncoder.encode(userDTO.getPassword());

        User user = new ModelMapper().map(userDTO, User.class);
        user.setPassword(password);
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

}

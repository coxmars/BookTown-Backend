package pe.todotic.bookstoreapi_s2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column(name = "fullname")
    private String fullName;
    private String email;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    @PrePersist
    void initCreatedAt () {
        createdAt = LocalDateTime.now();
        fullName = firstName + " " + lastName;
    }

    @PreUpdate
    void initUpdatedAt () {
        updatedAt = LocalDateTime.now();
        fullName = firstName + " " + lastName;
    }
}

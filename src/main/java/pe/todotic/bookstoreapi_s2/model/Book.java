package pe.todotic.bookstoreapi_s2.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String slug;
    @Column(name = "description") // Se dee mapear bien el nombre al usado en el atributo MySQL si no da error
    private String desc;
    private Float price;
    private String coverPath;
    private String filePath;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    @PrePersist
    void initCreatedAt () {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void initUpdatedAt () {
        updatedAt = LocalDateTime.now();
    }

}

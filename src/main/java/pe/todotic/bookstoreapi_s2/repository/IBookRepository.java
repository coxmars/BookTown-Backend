package pe.todotic.bookstoreapi_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.todotic.bookstoreapi_s2.model.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBookRepository extends JpaRepository<Book, Integer> {
    //List<Book> findTop6ByOrderByCreatedAtDesc();
    //List<Book> findLast6ByOrderByCreatedAt();
    Optional<Book> findOneBySlug(String slug);

    /* Esta es una alternativa al anterior usando JPQL, el 1? significa el 1 parametro que se envia en el metodo (String slug)
    @Query("select b from Book b where b.slug = 1?")
    Optional<Book> findSlug(String slug);
    */
}

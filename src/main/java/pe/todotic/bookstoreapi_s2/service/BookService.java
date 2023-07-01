package pe.todotic.bookstoreapi_s2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.todotic.bookstoreapi_s2.model.Book;

import java.util.List;

public interface BookService {
    List<Book> getBooks();

    Book save (Book book);

    void delete (Book book);

    Book getBook(Integer id);

    Book getBook(String slug);

    Page<Book> pageable (Pageable pageable);
}

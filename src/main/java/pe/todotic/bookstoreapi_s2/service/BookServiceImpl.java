package pe.todotic.bookstoreapi_s2.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.todotic.bookstoreapi_s2.model.Book;
import pe.todotic.bookstoreapi_s2.repository.IBookRepository;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private IBookRepository bookRepository;

    @Override
    @Transactional(readOnly = true) // Este metodo hace una conexión de solo lectura
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void delete(Book book) {
        bookRepository.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBook(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBook(String slug) {
        return bookRepository.findOneBySlug(slug)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> pageable(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
}

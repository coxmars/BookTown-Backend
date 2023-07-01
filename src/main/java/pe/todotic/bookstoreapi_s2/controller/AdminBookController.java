package pe.todotic.bookstoreapi_s2.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.todotic.bookstoreapi_s2.dto.BookDTO;
import pe.todotic.bookstoreapi_s2.model.Book;
import pe.todotic.bookstoreapi_s2.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/books")
public class AdminBookController {
    @Autowired
    private BookService bookService;

    /**
     * Devuelve la lista completa de libros
     * Retorna el status OK: 200
     * Ej.: GET <a href="http://localhost:9090/api/admin/books/list">...</a>
     */
    @GetMapping("/list")
    List<Book> list() {
        return bookService.getBooks();
    }

    /**
     * Devuelve un libro por su ID, en caso contrario
     * lanza EntityNotFoundException.
     * Retorna el status OK: 200
     * Ej.: GET <a href="http://localhost:9090/api/admin/books/find/1">...</a>
     */
    @GetMapping("/find/{id}")
    Book get(@PathVariable Integer id) {
        // Se retorna un objeto Book pero findById retorna un Optional
        return bookService.getBook(id);
    }

    /**
     * Crea un libro a partir del cuerpo
     * de la solicitud HTTP y retorna
     * el libro creado.
     * Retorna el status CREATED: 201
     * Ej.: POST <a href="http://localhost:9090/api/admin/books/save">...</a>
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/save")
    Book create(@Validated @RequestBody BookDTO bookDTO) {
        // Se envia como parametros source, destination que seria bookDTO donde viene la info y se va al objeto tipo book
        Book book = new ModelMapper().map(bookDTO, Book.class);
        return bookService.save(book);
    }

    /**
     * Actualiza un libro por su ID, a partir
     * del cuerpo de la solicitud HTTP.
     * Si el libro no es encontrado se lanza EntityNotFoundException.
     * Retorna el status OK: 200.
     * Ej.: PUT <a href="http://localhost:9090/api/admin/books/update/1">...</a>
     */
    @PutMapping("/update/{id}")
    Book update(@Validated @PathVariable Integer id, @RequestBody BookDTO bookDTO) {
        Book book = bookService.getBook(id);
        new ModelMapper().map(bookDTO, book); // En vez de Book.class se pasa book que viene de BD para actualizar
        return bookService.save(book);
    }

    /**
     * Elimina un libro por su ID.
     * Si el libro no es encontrado se lanza EntityNotFoundException.
     * Retorna el status NO_CONTENT: 204
     * Ej.: DELETE <a href="http://localhost:9090/api/admin/books/delete/1">...</a>
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete/{id}")
    void delete(@PathVariable Integer id) {
        Book book = bookService.getBook(id);
        bookService.delete(book);
    }

    /**
     * Devuelve la lista de libros de forma paginada.
     * El cliente puede enviar los parámetros page, size, sort,... en la URL
     * para configurar la página solicitada.
     * Si el cliente no envía ningún parámetro para la paginación,
     * se toma la configuración por defecto.
     * Retorna el status OK: 200
     * Ej.: GET <a href="http://localhost:9090/api/admin/books?page=0&size=2&sort=createdAt,desc">...</a>
     *
     * @param pageable la configuración de paginación que captura los parámetros como: page, size y sort
     */
    @GetMapping
    Page<Book> paginate(@PageableDefault(sort = "title", direction = Sort.Direction.ASC, size = 5) Pageable pageable) {
        return bookService.pageable(pageable);
    }

}

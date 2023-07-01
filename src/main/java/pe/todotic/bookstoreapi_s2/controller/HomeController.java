package pe.todotic.bookstoreapi_s2.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import pe.todotic.bookstoreapi_s2.model.Book;
import pe.todotic.bookstoreapi_s2.model.SalesOrder;
import pe.todotic.bookstoreapi_s2.paypal.OrderResponse;
import pe.todotic.bookstoreapi_s2.repository.ISalesOrderRepository;
import pe.todotic.bookstoreapi_s2.service.BookService;
import pe.todotic.bookstoreapi_s2.service.PaypalService;
import pe.todotic.bookstoreapi_s2.service.SalesOrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final BookService bookService;
    private final ISalesOrderRepository salesRepository;
    private final PaypalService paypalService;
    private final SalesOrderService salesOrderService;

    @Autowired
    public HomeController (BookService bookService, ISalesOrderRepository salesRepository, PaypalService paypalService, SalesOrderService salesOrderService) {
        this.bookService = bookService;
        this.salesRepository = salesRepository;
        this.paypalService = paypalService;
        this.salesOrderService = salesOrderService;
    }

    @GetMapping("/last-books")
    List<Book> getLastBooks () {
        Pageable pageable = PageRequest.of(0,6, Sort.by("createdAt").descending());
        return bookService.pageable(pageable).getContent();
        // return bookRepository.findTop6ByOrderByCreatedAtDesc();
    }

    @RequestMapping("/books")
    Page<Book> getBooks (@PageableDefault(sort = "title") Pageable pageable) {
        return bookService.pageable(pageable);
    }

    @RequestMapping("/books/{slug}")
    Book getBook (@PathVariable String slug) {
        return bookService.getBook(slug);
    }

    @RequestMapping("/orders/{id}")
    SalesOrder getSalesOrder (@PathVariable Integer id) {
        return salesRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @PostMapping("/checkout/paypal/create")
    Map<String, String> createPaypalCheckout (@RequestBody List<Integer> bookIds, @RequestParam String returnUrl) {
        SalesOrder salesOrder = salesOrderService.create(bookIds);
        OrderResponse orderResponse = paypalService.createOrder(salesOrder, returnUrl, returnUrl);
        String approveUrl = orderResponse
                .getLinks()
                .stream()
                .filter(link -> link.getRel().equals("approve"))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getHref();

        return Map.of("approveUrl", approveUrl);
    }

}

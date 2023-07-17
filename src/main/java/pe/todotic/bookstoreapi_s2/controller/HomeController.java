package pe.todotic.bookstoreapi_s2.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import pe.todotic.bookstoreapi_s2.exception.BadRequestException;
import pe.todotic.bookstoreapi_s2.model.Book;
import pe.todotic.bookstoreapi_s2.model.PaymentStatus;
import pe.todotic.bookstoreapi_s2.model.SalesItem;
import pe.todotic.bookstoreapi_s2.model.SalesOrder;
import pe.todotic.bookstoreapi_s2.paypal.OrderCaptureResponse;
import pe.todotic.bookstoreapi_s2.paypal.OrderResponse;
import pe.todotic.bookstoreapi_s2.repository.ISalesItemRepository;
import pe.todotic.bookstoreapi_s2.repository.ISalesOrderRepository;
import pe.todotic.bookstoreapi_s2.service.BookService;
import pe.todotic.bookstoreapi_s2.service.PaypalService;
import pe.todotic.bookstoreapi_s2.service.SalesOrderService;
import pe.todotic.bookstoreapi_s2.service.StorageService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final BookService bookService;
    private final ISalesOrderRepository salesOrderRepository;
    private final PaypalService paypalService;
    private final SalesOrderService salesOrderService;
    private final ISalesItemRepository salesItemRepository;
    private final StorageService storageService;

    @Autowired
    public HomeController (BookService bookService, ISalesOrderRepository salesOrderRepository, PaypalService paypalService, SalesOrderService salesOrderService,ISalesItemRepository salesItemRepository,StorageService storageService) {
        this.bookService = bookService;
        this.salesOrderRepository = salesOrderRepository;
        this.paypalService = paypalService;
        this.salesOrderService = salesOrderService;
        this.salesItemRepository = salesItemRepository;
        this.storageService = storageService;
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
        return salesOrderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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

    @PostMapping("/checkout/paypal/capture")
    Map<String, Object> capturePaypalCheckout(@RequestParam String token) {
        OrderCaptureResponse orderCaptureResponse = paypalService.captureOrder(token);
        boolean completed = orderCaptureResponse.getStatus().equals("COMPLETED");
        int orderId = 0;

        if (completed) {
            orderId = Integer.parseInt(orderCaptureResponse.getPurchaseUnits().get(0).getReferenceId());
            SalesOrder salesOrder = salesOrderRepository
                    .findById(orderId)
                    .orElseThrow(RuntimeException::new);
            salesOrder.setPaymentStatus(PaymentStatus.PAID);
            salesOrderRepository.save(salesOrder);
        }
        return Map.of("completed", completed, "orderId", orderId);
    }

    @GetMapping("/orders/{orderId}/items/{itemId}/book/download")
    Resource downloadBookFromSalesItem(
            @PathVariable Integer orderId,
            @PathVariable Integer itemId
    ) {
        SalesOrder salesOrder = salesOrderRepository
                .findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        if (!salesOrder.getPaymentStatus().equals(PaymentStatus.PAID)) {
            throw new BadRequestException("The order hasn't been paid yet.");
        }
        SalesItem salesItem = salesItemRepository
                .findOneByIdAndOrderId(itemId, orderId)
                .orElseThrow(EntityNotFoundException::new);

        if (salesItem.getDownloadsAvailable() > 0) {
            salesItem.setDownloadsAvailable(
                    salesItem.getDownloadsAvailable() - 1
            );
            salesItemRepository.save(salesItem);
        } else {
            throw new BadRequestException("Can't download this file anymore.");
        }
        return storageService.loadAsResource(salesItem.getBook().getFilePath());
    }

}

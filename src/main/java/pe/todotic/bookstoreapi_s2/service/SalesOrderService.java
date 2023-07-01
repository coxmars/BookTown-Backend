package pe.todotic.bookstoreapi_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.todotic.bookstoreapi_s2.model.Book;
import pe.todotic.bookstoreapi_s2.model.PaymentStatus;
import pe.todotic.bookstoreapi_s2.model.SalesItem;
import pe.todotic.bookstoreapi_s2.model.SalesOrder;
import pe.todotic.bookstoreapi_s2.repository.ISalesOrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalesOrderService {
    private final BookService bookService;
    private final ISalesOrderRepository salesOrderRepository;

    @Autowired
    public SalesOrderService (BookService bookService, ISalesOrderRepository salesOrderRepository) {
        this.bookService = bookService;
        this.salesOrderRepository = salesOrderRepository;
    }

    public SalesOrder create (List<Integer> bookIds) {
        SalesOrder salesOrder = new SalesOrder();
        List<SalesItem> items = new ArrayList<>();
        float total = 0;

        for (int bookId : bookIds) {
            Book book = bookService.getBook(bookId);

            SalesItem salesItem = new SalesItem();
            salesItem.setBook(book);
            salesItem.setPrice(book.getPrice());
            salesItem.setDownloadsAvailable(3);
            salesItem.setOrder(salesOrder);

            total += salesItem.getPrice();
            items.add(salesItem);
        }

        salesOrder.setItems(items);
        salesOrder.setTotal(total);
        salesOrder.setPaymentStatus(PaymentStatus.PENDING);
        salesOrder.setCreatedAt(LocalDateTime.now());

        return salesOrderRepository.save(salesOrder);
    }

}

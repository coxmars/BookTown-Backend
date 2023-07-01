package pe.todotic.bookstoreapi_s2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Float total;
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User customer;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SalesItem> items;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    @PrePersist
    void initCreatedAt () {
        createdAt = LocalDateTime.now();
    }
}

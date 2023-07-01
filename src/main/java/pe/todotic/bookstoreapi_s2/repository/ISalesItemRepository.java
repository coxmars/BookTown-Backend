package pe.todotic.bookstoreapi_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.todotic.bookstoreapi_s2.model.SalesItem;

public interface ISalesItemRepository extends JpaRepository<SalesItem, Integer> {

}

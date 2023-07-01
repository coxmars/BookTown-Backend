package pe.todotic.bookstoreapi_s2.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderCaptureResponse {
    private String id;
    private String status;

    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;
}

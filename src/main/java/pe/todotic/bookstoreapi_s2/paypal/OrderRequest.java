package pe.todotic.bookstoreapi_s2.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @JsonProperty("application_context")
    private ApplicationContext applicationContext;

    private Intent intent;

    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;

    public enum Intent {
        CAPTURE
    }
}

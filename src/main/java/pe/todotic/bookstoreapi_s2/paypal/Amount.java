package pe.todotic.bookstoreapi_s2.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
public class Amount {
    @JsonProperty("currency_code")
    private CurrencyCode currencyCode;

    private String value;
    private Breakdown breakdown;

    public enum CurrencyCode {
        USD
    }

    @Data
    @RequiredArgsConstructor
    public static class Breakdown {
        @NonNull
        @JsonProperty("item_total")
        private Amount itemTotal;
    }
}

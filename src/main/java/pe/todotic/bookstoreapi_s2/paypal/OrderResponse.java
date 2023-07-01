package pe.todotic.bookstoreapi_s2.paypal;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponse {
    private String id;
    private String status;
    private List<Link> links;

    @Data
    public static class Link {
        private String href;
        private String rel;
        private String method;
    }
}

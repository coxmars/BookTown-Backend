package pe.todotic.bookstoreapi_s2.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pe.todotic.bookstoreapi_s2.model.Book;
import pe.todotic.bookstoreapi_s2.model.SalesOrder;
import pe.todotic.bookstoreapi_s2.paypal.*;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class PaypalService {
    private final static String PAYPAL_API_BASE = "";
    private final static String PAYPAL_CLIENT_ID = "";
    private final static String PAYPAL_CLIENT_SECRET = "";

    private String getAccessToken () {
        String url = String.format("%s/v1/oauth2/token/", PAYPAL_API_BASE);
        RestTemplate restTemplate = new RestTemplate(); // Lo usamos para realizar el intercambio de info entre servidores

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(PAYPAL_CLIENT_ID, PAYPAL_CLIENT_SECRET);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        ResponseEntity<Token> response = restTemplate.postForEntity(url, entity, Token.class);

        return response.getBody().getAccessToken();
    }

    public OrderResponse createOrder (SalesOrder salesOrder, String returnUrl, String cancelUrl) {
        String url = String.format("%s/v1/oauth2/token/", PAYPAL_API_BASE);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setIntent(OrderRequest.Intent.CAPTURE);

        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.setBrandName("The BookTown");
        applicationContext.setReturnUrl(returnUrl);
        applicationContext.setCancelUrl(cancelUrl);

        orderRequest.setApplicationContext(applicationContext);

        // Create single purchase unit of sales order
        PurchaseUnit purchaseUnit = new PurchaseUnit();
        purchaseUnit.setReferenceId(salesOrder.getId().toString());

        Amount purchaseAmount = new Amount();
        purchaseAmount.setCurrencyCode(Amount.CurrencyCode.USD);
        purchaseAmount.setValue(salesOrder.getTotal().toString());

        Amount itemsAmount = new Amount();
        itemsAmount.setCurrencyCode(Amount.CurrencyCode.USD);
        itemsAmount.setValue(salesOrder.getTotal().toString());

        purchaseAmount.setBreakdown(new Amount.Breakdown(itemsAmount));

        purchaseUnit.setAmount(purchaseAmount);
        purchaseUnit.setItems(new ArrayList<>());

        salesOrder.getItems().forEach(itemOrder -> {
            Book book = itemOrder.getBook();
            OrderItem orderItem = new OrderItem();
            orderItem.setName(book.getTitle());
            orderItem.setSku(book.getId().toString());
            orderItem.setQuantity("1");

            Amount unitAmount = new Amount();
            unitAmount.setCurrencyCode(Amount.CurrencyCode.USD);
            unitAmount.setValue(itemOrder.getPrice().toString());

            orderItem.setUnitAmount(unitAmount);
            purchaseUnit.getItems().add(orderItem);
        });

        orderRequest.setPurchaseUnits(Collections.singletonList(purchaseUnit));

        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<OrderRequest> entity = new HttpEntity<>(orderRequest, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(url, entity, OrderResponse.class);

        return response.getBody();
    }

    public OrderCaptureResponse captureOrder(String orderId) {
        String url = String.format("%s/v2/checkout/orders/%s/capture", PAYPAL_API_BASE, orderId);

        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderRequest> entity = new HttpEntity<>(null, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OrderCaptureResponse> response = restTemplate
                .postForEntity(url, entity, OrderCaptureResponse.class);
        return response.getBody();
    }

}

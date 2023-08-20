package com.imvux21.orderservice.service;

import com.imvux21.orderservice.dto.OrderLineItemDTO;
import com.imvux21.orderservice.dto.request.CreateOrderRequest;
import com.imvux21.orderservice.dto.response.InventoryResponse;
import com.imvux21.orderservice.entity.Order;
import com.imvux21.orderservice.entity.OrderLineItem;
import com.imvux21.orderservice.event.OrderPlacedEvent;
import com.imvux21.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String createOrder(CreateOrderRequest createOrderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        
        List<OrderLineItem> orderLineItemList = createOrderRequest.getOrderLineItemDTOList().stream()
                .map(this::mapToOrderLineItem)
                .toList();
        order.setOrderLineItems(orderLineItemList);

        List<String> skuCodeList = orderLineItemList.stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        // call inventory service to check if the items are available
        InventoryResponse[] isInStock = webClientBuilder.build()
                .get()
                // uri() method is used to build the URI. It takes a Function<UriBuilder, URI> as an argument.
                // queryParam() method is used to add query parameters to the URI. It takes a key and a value as arguments.
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodeList)
                                                .build()
                )
                .retrieve()
                .bodyToMono(InventoryResponse[].class)// If you are retrieving a single item for response, use bodyToMono. It emits 0-1 items
                                          // For multiple items, use bodyToFlux. It emits 0-N items.
                .block(); // block() is a synchronous call. It blocks the current thread until the response is received (default is async call)

        // check if all items are in stock
        boolean allProductsInStock = Arrays.stream(Objects.requireNonNull(isInStock)).allMatch(InventoryResponse::isInStock);

        if (!allProductsInStock) {
            log.error("Some of the products are out of stock");
            throw new RuntimeException("Some of the products are out of stock");
        }

        orderRepository.save(order);
        kafkaTemplate.send("notification-topic", new OrderPlacedEvent(order.getOrderNumber()));
        return "Order created successfully";
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemDTO orderLineItemDTO) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSkuCode(orderLineItemDTO.getSkuCode());
        orderLineItem.setPrice(orderLineItemDTO.getPrice());
        orderLineItem.setQuantity(orderLineItemDTO.getQuantity());
        return orderLineItem;
    }
}

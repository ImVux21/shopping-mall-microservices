package com.imvux21.orderservice.dto.request;

import com.imvux21.orderservice.dto.OrderLineItemDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private List<OrderLineItemDTO> orderLineItemDTOList;
}

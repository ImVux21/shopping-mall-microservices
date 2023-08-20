package com.imvux21.inventoryservice.controller;

import com.imvux21.inventoryservice.dto.response.InventoryResponse;
import com.imvux21.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        log.info("Server port: {}", System.getenv("SERVER_PORT"));
        return inventoryService.isInStock(skuCode);
    }
}

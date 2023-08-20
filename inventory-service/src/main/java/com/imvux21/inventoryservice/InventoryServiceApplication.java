package com.imvux21.inventoryservice;

import com.imvux21.inventoryservice.entity.Inventory;
import com.imvux21.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return (args) -> {
            Inventory inventory1 = new Inventory();
            inventory1.setSkuCode("xuc_xich_1");
            inventory1.setQuantity(10);

            Inventory inventory2 = new Inventory();
            inventory2.setSkuCode("xuc_xich_2");
            inventory2.setQuantity(0);


            if (inventoryRepository.findBySkuCodeIn(List.of(inventory1.getSkuCode(), inventory2.getSkuCode())).isEmpty()) {
                inventoryRepository.saveAll(List.of(inventory1, inventory2));
            }

        };
    }
}

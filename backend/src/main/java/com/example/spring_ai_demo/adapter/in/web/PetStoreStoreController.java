package com.example.spring_ai_demo.adapter.in.web;

import com.example.petstore.api.StoreApi;
import com.example.petstore.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pet-store")
public class PetStoreStoreController implements StoreApi {
    @Override
    public ResponseEntity<Void> deleteOrder(String orderId) {
        return null;
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getInventory() {
        return null;
    }

    @Override
    public ResponseEntity<Order> getOrderById(Long orderId) {
        return null;
    }

    @Override
    public ResponseEntity<Order> placeOrder(Order order) {
        return null;
    }
}

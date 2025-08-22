package com.example.orderservice.dto.request;

public record RestoreProductRequest(
        Long id, Integer restoreQuantity
) {
}

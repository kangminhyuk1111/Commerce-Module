package com.example.productservice.dto.request;

public record RestoreProductRequest(
        Long id, Integer restoreQuantity
) {
}

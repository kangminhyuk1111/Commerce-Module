package com.example.productservice.repository;

import com.example.productservice.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
  Product save(Product product);
  Optional<Product> findById(Long id);
  List<Product> findAll();
  void deleteById(Long id);
  Optional<Product> findByIdForUpdate(Long id);
}

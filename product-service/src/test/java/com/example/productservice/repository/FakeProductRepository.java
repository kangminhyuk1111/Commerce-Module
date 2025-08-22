package com.example.productservice.repository;

import com.example.productservice.entity.Product;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class FakeProductRepository implements ProductRepository {

  private final Map<Long, Product> store = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  private final Map<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

  @Override
  public Product save(Product product) {
    if (product.getId() == null) {
      Long newId = idGenerator.getAndIncrement();

      Product newProduct = new Product(
          product.getName(),
          product.getPrice(),
          product.getStock()
      );

      setProductId(newProduct, newId);
      store.put(newId, newProduct);
      return newProduct;
    } else {
      store.put(product.getId(), product);
      return product;
    }
  }

  @Override
  public Optional<Product> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public List<Product> findAll() {
    return new ArrayList<>(store.values());
  }

  @Override
  public void deleteById(Long id) {
    store.remove(id);
    lockMap.remove(id);
  }

  @Override
  public Optional<Product> findByIdForUpdate(Long id) {
    ReentrantLock lock = lockMap.computeIfAbsent(id, k -> new ReentrantLock());

    lock.lock();
    try {
      return Optional.ofNullable(store.get(id));
    } finally {
      lock.unlock();
    }
  }

  public void clear() {
    store.clear();
    lockMap.clear();
    idGenerator.set(1);
  }

  public int size() {
    return store.size();
  }

  private void setProductId(Product product, Long id) {
    try {
      Field idField = Product.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(product, id);
    } catch (Exception e) {
      throw new RuntimeException("ID 설정 실패", e);
    }
  }
}
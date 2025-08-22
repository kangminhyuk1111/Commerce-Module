package com.example.pointservice.repository;

import com.example.pointservice.domain.Point;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakePointRepository implements PointRepository {

  private final Map<Long, Point> storage = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1L);

  @Override
  public Optional<Point> findByUserId(Long userId) {
    return storage.values().stream()
        .filter(point -> point.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public Optional<Point> findByUserIdWithLock(Long userId) {
    return findByUserId(userId);
  }

  @Override
  public Point save(Point point) {
    if (point.getId() == null) {
      Point newPoint = new Point(
          idGenerator.getAndIncrement(),
          point.getUserId(),
          point.getBalance(),
          LocalDateTime.now()
      );
      storage.put(newPoint.getId(), newPoint);
      return newPoint;
    } else {
      storage.put(point.getId(), point);
      return point;
    }
  }

  @Override
  public void deleteAll() {
    storage.clear();
    idGenerator.set(1L);
  }

  public int size() {
    return storage.size();
  }

  public void clear() {
    deleteAll();
  }
}

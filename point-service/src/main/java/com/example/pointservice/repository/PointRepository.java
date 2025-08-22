package com.example.pointservice.repository;

import com.example.pointservice.domain.Point;
import java.util.Optional;

public interface PointRepository {

  Optional<Point> findByUserId(Long userId);

  Optional<Point> findByUserIdWithLock(Long userId);

  Point save(Point point);

  void deleteAll();
}

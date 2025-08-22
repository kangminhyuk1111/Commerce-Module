package com.example.pointservice.dto.response;

import com.example.pointservice.domain.Point;
import java.time.LocalDateTime;

public record PointResponse(
    Long id, Long userId, Integer balance, LocalDateTime lastUpdated
) {

  public static PointResponse of(final Point point) {
    return new PointResponse(
        point.getId(), point.getUserId(), point.getBalance(), point.getLastUpdated()
    );
  }
}

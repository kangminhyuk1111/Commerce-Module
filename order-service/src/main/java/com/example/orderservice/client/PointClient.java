package com.example.orderservice.client;

import com.example.orderservice.dto.request.DeductPointsRequest;
import com.example.orderservice.dto.response.DeductPointsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "point-client", url = "${point-service.api-url}")
public interface PointClient {

  @PostMapping("/deduct")
  DeductPointsResponse deductPoints(@RequestBody DeductPointsRequest request);
}

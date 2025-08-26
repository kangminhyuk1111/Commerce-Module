package com.example.pointservice.controller;

import com.example.pointservice.dto.request.AddPointRequest;
import com.example.pointservice.dto.request.CreateAccountRequest;
import com.example.pointservice.dto.request.RefundPointRequest;
import com.example.pointservice.dto.request.UsePointRequest;
import com.example.pointservice.dto.response.PointResponse;
import com.example.pointservice.service.PointService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points")
public class PointController {

  private final PointService pointService;

  public PointController(final PointService pointService) {
    this.pointService = pointService;
  }

  // UserId를 통해 포인트 조회
  @GetMapping("/{userId}")
  public PointResponse findPointByUserId(@PathVariable Long userId) {
    return pointService.findPointByUserId(userId);
  }

  // 포인트 추가
  @PostMapping("/add")
  public PointResponse addPointByUserId(@RequestBody AddPointRequest request) {
    return pointService.addPoint(request);
  }

  // 포인트 사용
  @PostMapping("/use")
  public PointResponse usePointByUserId(@RequestBody UsePointRequest request) {
    return pointService.usePoint(request);
  }

  // 포인트 환불
  @PostMapping("/refund")
  public PointResponse refundPointByUserId(@RequestBody RefundPointRequest request) {
    return pointService.refundPoint(request);
  }
}

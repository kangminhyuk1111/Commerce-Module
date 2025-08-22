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

  @GetMapping("/{id}")
  public PointResponse findPointByUserId(@PathVariable Long id) {
    return pointService.findPointByUserId(id);
  }

  @PostMapping("/add")
  public PointResponse addPointByUserId(@RequestBody AddPointRequest request) {
    return pointService.addPoint(request);
  }

  @PostMapping("/use")
  public PointResponse usePointByUserId(@RequestBody UsePointRequest request) {
    return pointService.usePoint(request);
  }

  @PostMapping("/refund")
  public PointResponse refundPointByUserId(@RequestBody RefundPointRequest request) {
    return pointService.refundPoint(request);
  }
}

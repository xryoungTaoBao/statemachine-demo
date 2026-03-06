package com.yubzhou.statemachine.statistics.controller;

import com.yubzhou.statemachine.common.result.Result;
import com.yubzhou.statemachine.statistics.service.OrderStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller exposing order statistics.
 */
@Tag(name = "Order Statistics API", description = "Order statistics and reporting")
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class OrderStatisticsController {

    private final OrderStatisticsService statisticsService;

    @Operation(summary = "Get count of orders grouped by state")
    @GetMapping("/state-counts")
    public Result<Map<String, Long>> stateCounts() {
        return Result.success(statisticsService.countByState());
    }

    @Operation(summary = "Get total number of orders")
    @GetMapping("/total")
    public Result<Long> total() {
        return Result.success(statisticsService.totalCount());
    }
}

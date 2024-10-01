package com.my_task.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my_task.service.statistic.StatisticService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@RestController
@RequestMapping("/api/statistics")
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping()
    public ResponseEntity<?> getMethodName() {
        return ResponseEntity.ok(statisticService.getStatistics());
    }

}

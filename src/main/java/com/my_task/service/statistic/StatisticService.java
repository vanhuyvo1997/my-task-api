package com.my_task.service.statistic;

import org.springframework.stereotype.Service;

import com.my_task.repository.TaskRepository;
import com.my_task.repository.TaskStatistics;
import com.my_task.repository.UserStatistics;
import com.my_task.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class StatisticService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public StatisticsResponse getStatistics() {
        return new StatisticsResponse(userRepository.getUserStatistics(), taskRepository.getTaskStatistics());
    }

    private record StatisticsResponse(UserStatistics userStatistics, TaskStatistics taskStatistics) {
    }
}

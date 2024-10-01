package com.my_task.repository;

public interface TaskStatistics {
    Long getTotalTasks();

    Long getCompletedTasks();

    Long getTodoTasks();
}

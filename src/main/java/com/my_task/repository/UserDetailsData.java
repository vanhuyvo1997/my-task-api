package com.my_task.repository;

public interface UserDetailsData {
    String getId();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getAvatarUrl();

    Boolean getEnabled();

    int getTotalTasks();

    int getNumOfTodoTasks();

    int getNumOfCompletedTasks();
}

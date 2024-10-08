package com.my_task.repository;

public interface UserDetailsData {
    String getId();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getAvatarUrl();

    Boolean getEnabled();

    Integer getTotalTasks();

    Integer getNumOfTodoTasks();

    Integer getNumOfCompletedTasks();
}

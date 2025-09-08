package org.shetsko.service;

import org.shetsko.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TaskIdGenerator {
    @Autowired
    private TaskRepository taskRepository;

    public String generateTaskId() {
        LocalDate today = LocalDate.now();
        String datePart = String.format("%02d%02d",
                today.getDayOfMonth(), today.getMonthValue());

        long tasksToday = taskRepository.countByCreatedAtBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        return datePart + String.format("%04d", tasksToday + 1);
    }
}

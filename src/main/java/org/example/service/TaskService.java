package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.ProjectDao;
import org.example.dao.TaskDao;
import org.example.model.Task;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskService {
    private static final Logger logger = LogManager.getLogger(TaskService.class);
    private TaskDao taskDao;
    private ProjectDao projectDao;

    public TaskService(TaskDao taskDao, ProjectDao projectDao) {
        this.taskDao = taskDao;
        this.projectDao = projectDao;
    }

    public void validateTasks(List<Task> tasks) throws SQLException {
        List<Task> validTasks = new ArrayList<>();
        Set<Integer> taskIdSet = new HashSet<>();
        int lineNumber = 1;

        for (Task task : tasks) {
            if (task == null) {
                logger.warn("Skipping null task on line {}", lineNumber);
                lineNumber++;
                continue;
            }

            if (isTaskValid(task, taskIdSet, lineNumber)) {
                validTasks.add(task);
            } else {
                logger.debug("Task failed validation: {}", task);
            }
            lineNumber++;
        }

        // Inserting valid tasks
        if (!validTasks.isEmpty()) {
            taskDao.insertTasks(validTasks);
            logger.info("Successfully inserted {} valid tasks into the database.", validTasks.size());
        } else {
            logger.warn("No valid tasks to insert.");
        }
    }

    private boolean isTaskValid(Task task, Set<Integer> taskIdSet, int lineNumber) throws SQLException {
        return isTaskNameInvalid(task, lineNumber) &&
                isProjectIdValid(task, lineNumber) &&
                isTaskStatusValid(task, lineNumber) &&
                isProjectExists(task, lineNumber) &&
                isTaskIdValid(task, taskIdSet, lineNumber);
    }

    private boolean isTaskIdValid (Task task, Set <Integer> taskIdSet, int lineNumber) {
        if (task.getTaskId() <= 0) {
            logger.error("Invalid Task ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }

        if (taskIdSet.contains(task.getTaskId())) {
            logger.error("Duplicate Resource ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        taskIdSet.add(task.getTaskId());
        return true;
    }

    private boolean isTaskNameInvalid(Task task, int lineNumber) {
        if (task.getTaskName() == null || task.getTaskName().trim().isEmpty()) {
            logger.error("Invalid Task Name on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isProjectIdValid(Task task, int lineNumber) {
        if (task.getProjectId() <= 0) {
            logger.error("Invalid Project ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isTaskStatusValid(Task task, int lineNumber) {
        if (!isValidStatus(task.getTaskStatus())) {
            logger.error("Invalid Status: {}", task.getTaskStatus());
            logger.error("Invalid Status on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isProjectExists(Task task, int lineNumber) throws SQLException {
        if (!projectDao.doesProjectExist(task.getProjectId())) {
            logger.error("Invalid ProjectId: {}", task.getProjectId());
            logger.error("Skipping invalid task on line {} - {} Invalid field: ProjectId (not found in project table)", lineNumber, getTaskDetails(task));
            return false;
        }
        return true;
    }

    private boolean isValidStatus(String status) {
        return "In Progress".equalsIgnoreCase(status) ||
                "Not Started".equalsIgnoreCase(status) ||
                "Completed".equalsIgnoreCase(status);
    }

    private String getTaskDetails(Task task) {
        return "Task ID: " + task.getTaskId();
    }
}
package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Task;
import org.example.service.TaskService;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TaskController extends BaseController {
    private static final Logger logger = LogManager.getLogger(TaskController.class);
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public void readFromCsv(String filePath) throws IOException, SQLException {
        List<Task> tasks = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);

        if (inputStream == null) {
            logger.error("File not found in tasks folder: {}", filePath);
            throw new FileNotFoundException("File not found in tasks folder: " + filePath);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            // Skipping header
            br.readLine();
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] column = line.split(",");

                try {
                    if (column.length < 4) {
                        logger.warn("Invalid number of Task columns in line {}. Skipping this entry.", lineNumber);
                        continue;
                    }

                    Integer taskId = parseInteger(column[0]);
                    String taskName = parseString(column[1]);
                    Integer projectId = parseInteger(column[2]);
                    String taskStatus = column[3].trim();

                    Task task = new Task(taskId, taskName, projectId, taskStatus);
                    tasks.add(task);

                } catch (Exception e) {
                    logger.error("Error processing line {}: {}", lineNumber, e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading the input stream: {}", e.getMessage());
        }
        logger.info("Finished reading CSV file. Attempting to validate {} tasks.", tasks.size());
        taskService.validateTasks(tasks);
    }
}
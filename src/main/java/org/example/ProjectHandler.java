package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.ProjectDao;
import org.example.dao.TaskDao;
import org.example.model.Project;
import org.example.model.ProjectResponse;
import org.example.model.Task;
import org.example.service.ProjectService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class ProjectHandler implements HttpHandler {
    private static final Logger logger = LogManager.getLogger(ProjectHandler.class);
    private final ProjectService projectService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProjectHandler(Connection connection) {
        this.projectService = new ProjectService(new ProjectDao(connection),new TaskDao(connection));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response;
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        if (method.equals("GET") && path.matches("/projects/\\d+/tasks")) {
            int projectId = Integer.parseInt(path.split("/")[2]);
            try {
                response = getProjectWithTasks(projectId);
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } catch (SQLException e) {
                response = "Error fetching project: " + e.getMessage();
                exchange.sendResponseHeaders(500, response.getBytes().length);
            }
        } else if (method.equals("POST") && path.matches("/projects/\\d+/tasks")) {
            int projectId = Integer.parseInt(path.split("/")[2]);
            String requestBody = getRequestBody(exchange);
            response = addTaskToProject(projectId, requestBody);
            exchange.sendResponseHeaders(201, response.getBytes().length); // 201 Created
        } else if (method.equals("PUT") && path.matches("/projects/\\d+/tasks/\\d+")) {
            String[] pathParts = path.split("/");
            int projectId = Integer.parseInt(pathParts[2]);
            int taskId = Integer.parseInt(pathParts[4]);
            String requestBody = getRequestBody(exchange);
            response = updateTaskInProject(projectId, taskId, requestBody);
            exchange.sendResponseHeaders(200, response.getBytes().length); // 200 OK
        } else {
            response = "Invalid request";
            exchange.sendResponseHeaders(404, response.getBytes().length); // 404 Not Found
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String getProjectWithTasks(int projectId) throws SQLException, IOException {
        logger.info("Received request to fetch project with tasks. Project ID: {}", projectId);
        try {
            // Fetch the project with tasks using the service
            Project project = projectService.getProjectWithTasks(projectId);

            // Log the fetched project details
            logger.info("Successfully fetched project details for Project ID: {}", projectId);
            return objectMapper.writeValueAsString(new ProjectResponse(project));
        } catch (SQLException e) {
            logger.error("SQL error while fetching project with ID {}: {}", projectId, e.getMessage(), e);
            throw e; // Re-throw to send a proper HTTP response in the `handle` method
        } catch (Exception e) {
            logger.error("Unexpected error while fetching project with ID {}: {}", projectId, e.getMessage(), e);
            throw new IOException("Unexpected error occurred", e);
        }
    }

    private String addTaskToProject(int projectId, String requestBody) {
        logger.info("Received request to add a task to project. Project ID: {}", projectId);
        logger.info("Request Body: {}", requestBody);

        try {
            // Parse the JSON request body into a Task object
            Task newTask = objectMapper.readValue(requestBody, Task.class);
            logger.info("Parsed task details: {}", newTask);

            // Associate the task with the project ID
            newTask.setProjectId(projectId);

            // Call the service to add the task
            projectService.addTaskToProject(projectId, newTask);
            logger.info("Task added successfully to Project ID: {}. Task details: {}", projectId, newTask);

            return "Task added successfully";
        } catch (IOException e) {
            logger.error("Error parsing JSON while adding task to Project ID {}: {}", projectId, e.getMessage(), e);
            return "Error adding task: Invalid JSON format";
        } catch (SQLException e) {
            logger.error("SQL error while adding task to Project ID {}: {}", projectId, e.getMessage(), e);
            return "Error adding task: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error while adding task to Project ID {}: {}", projectId, e.getMessage(), e);
            return "Unexpected error occurred while adding task";
        }
    }


    private String getRequestBody(HttpExchange exchange) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return requestBody.toString();
    }

    private String updateTaskInProject(int projectId, int taskId, String requestBody) {
        StringBuilder updateResult = new StringBuilder();
        try {
            // Log the incoming request body
            logger.info("Received update request for Task ID: {} in Project ID: {} with body: {}", taskId, projectId, requestBody);

            // Parse the requestBody for fields to update
            Map<String, Object> updates = objectMapper.readValue(requestBody, Map.class);

            // Loop through each field and update it
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

                // Log the field being updated and its new value
                logger.info("Updating Task ID: {} Field: {} to value: {}", taskId, fieldName, fieldValue);

                // Use the service to update each field
                String result = projectService.updateTaskField(taskId, fieldName, fieldValue);
                updateResult.append(fieldName).append(": ").append(result).append("\n");
            }

            // Log the result of the update operation
            logger.info("Update operation result for Task ID {}: {}", taskId, updateResult.toString());
        } catch (IOException e) {
            logger.error("Error parsing request body: {}", e.getMessage(), e);
            return "Error updating task: Invalid JSON format";
        } catch (Exception e) {
            logger.error("Error updating task: {}", e.getMessage(), e);
            return "Unexpected error occurred while updating task";
        }

        return updateResult.toString();
    }
}

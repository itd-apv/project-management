package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.controller.*;
import org.example.dao.*;
import org.example.service.*;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.Properties;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static Connection connection; // Declare connection at the class level

    public static void main(String[] args) {
        logger.info("Application starting...");

        String resourcePath = "itdesignSqlAssignment - Sheet1.csv";
        String projectPath = "itdesignSqlAssignment - Sheet2.csv";
        String taskPath = "itdesignSqlAssignment - Sheet3.csv";
        String assignmentPath = "itdesignSqlAssignment - Sheet4.csv";

        // Load properties from the db.properties file located in the resources folder
        Properties properties = new Properties();

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
                return;
            }
            // Load the properties from the file
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Retrieve database details from the properties file
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");


        try {
            connection = DriverManager.getConnection(url, user, password); // Keep connection open
            logger.info("Database connection established.");

            ResourceDao resourceDao = new ResourceDao(connection);
            ResourceService resourceService = new ResourceService(resourceDao);
            ResourceController resourceController = new ResourceController(resourceService);
            resourceController.readFromCsv(resourcePath);

            ProjectDao projectDao = new ProjectDao(connection);
            TaskDao taskDao = new TaskDao(connection); // Instantiate TaskDao here
            ProjectService projectService = new ProjectService(projectDao, taskDao); // Pass taskDao to ProjectService
            ProjectController projectController = new ProjectController(projectService);
            projectController.readFromCsv(projectPath);

            TaskService taskService = new TaskService(taskDao, projectDao); // Keep as is
            TaskController taskController = new TaskController(taskService);
            taskController.readFromCsv(taskPath);

            AssignmentDao assignmentDao = new AssignmentDao(connection);
            AssignmentService assignmentService = new AssignmentService(assignmentDao, resourceDao, taskDao);
            AssignmentController assignmentController = new AssignmentController(assignmentService);
            assignmentController.readFromCsv(assignmentPath);

            logger.info("Data insertion completed.");

            // Start the HTTP server after data insertion
            startHttpServer();
        } catch (SQLException e) {
            logger.error("Database connection error: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("File read error: {}", e.getMessage(), e);
        } finally {
            // Ensure the connection is closed when the application terminates
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                        logger.info("Database connection closed.");
                    }
                } catch (SQLException e) {
                    logger.error("Error closing database connection: {}", e.getMessage(), e);
                }
            }));
        }
    }

    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/projects", new ProjectHandler(connection)); // Use the connection for project requests
        server.setExecutor(null); // Creates a default executor
        server.start();
        logger.info("Server started on port 8080");
    }
}
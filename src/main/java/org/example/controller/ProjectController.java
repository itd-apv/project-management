package org.example.controller;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Project;
import org.example.service.ProjectService;
import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProjectController extends BaseController {
    private static final Logger logger = LogManager.getLogger(ProjectController.class);
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void readFromCsv(String filePath) throws IOException, SQLException {
        List<Project> projects = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);

        if (inputStream == null) {
            logger.error("File not found in projects folder: {}", filePath);
            throw new FileNotFoundException("File not found in projects folder: " + filePath);
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             //OpenCSV dependency doesn't need buffered reader, automatically deals with common complexities and splits each line
             CSVReader csvReader = new CSVReader(inputStreamReader)) {

            String[] column;
            int lineNumber = 0;

            // Skipping the header row
            csvReader.readNext();

            while ((column = csvReader.readNext()) != null) {
                lineNumber++;
                try {
                    if (column.length < 6) {
                        logger.warn("Invalid number of Project columns in line {}. Skipping this entry.", lineNumber);
                        continue;
                    }

                    Integer projectId = parseInteger(column[0]);
                    String projectName = parseString(column[1]);
                    Timestamp projectStart = parseTimestamp(column[2]);
                    Timestamp projectFinish = parseTimestamp(column[3]);
                    Timestamp projectCreatedDate = parseTimestamp(column[4]);
                    Boolean projectActive = parseBoolean(column[5]);

                    Project project = new Project(projectId, projectName, projectStart, projectFinish, projectCreatedDate, projectActive);
                    projects.add(project);

                } catch (Exception e) {
                    logger.error("Error processing line {}: {}", lineNumber, e.getMessage());
                }
            }

        } catch (IOException | CsvValidationException e) {
            logger.error("Error reading CSV file: {}", e.getMessage());
        }

        logger.info("Finished reading CSV file. Attempting to validate {} projects.", projects.size());
        projectService.validateProjects(projects);
    }
}

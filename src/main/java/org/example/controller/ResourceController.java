package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.service.ResourceService;
import org.example.model.Resource;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceController extends BaseController {
    private static final Logger logger = LogManager.getLogger(ResourceController.class);
    private ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public void readFromCsv(String filePath) throws IOException, SQLException {
        List<Resource> resources = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);

        if (inputStream == null) {
            logger.error("File not found in resources folder: {}", filePath);
            throw new FileNotFoundException("File not found in resources folder: " + filePath);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            br.readLine(); // Skipping header
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] column = line.split(",");

                try {
                    if (column.length < 3) {
                        logger.warn("Invalid number of Resource columns in line {}. Skipping this entry.", lineNumber);
                        continue;
                    }

                    Integer resourceId = parseInteger(column[0]);
                    String resourceName = parseString(column[1]);
                    Boolean resourceActive = parseBoolean(column[2]);

                    Resource resource = new Resource(resourceId, resourceName, resourceActive);
                    resources.add(resource);

                } catch (Exception e) {
                    logger.error("Error processing line {}: {}", lineNumber, e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading the input stream: {}", e.getMessage());
        }
        logger.info("Finished reading CSV file. Attempting to validate {} resources.", resources.size());
        resourceService.validateResources(resources);
    }
}

package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.model.Assignment;
import org.example.service.AssignmentService;
import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssignmentController extends BaseController {
    private static final Logger logger = LogManager.getLogger(AssignmentController.class);
    private AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    public void readFromCsv(String filePath) throws IOException, SQLException {
        List<Assignment> assignments = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);

        if (inputStream == null) {
            logger.error("File not found in assignments folder: {}", filePath);
            throw new FileNotFoundException("File not found in assignments folder: " + filePath);
        }
        // Using Apache Commons CSV to read the CSV file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             //Apache commons CSV dependency handles delimiters, splits each row and detects headers
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.Builder.create()
                             .setHeader() // Auto-detects header
                             .setSkipHeaderRecord(true) // Skips the header row
                             .build())) {

            int lineNumber = 0; // Tracking line numbers

            // Iterating over the CSV records
            for (CSVRecord record : csvParser) {
                lineNumber++;

                try {
                    if (record.size() < 5) {
                        logger.warn("Invalid number of Assignment columns in line {}. Skipping this entry.", lineNumber);
                        continue;
                    }

                    // Parsing individual fields
                    Integer assignmentId = parseInteger(record.get(0));
                    Integer taskId = parseInteger(record.get(1));
                    Integer resourceId = parseInteger(record.get(2));
                    BigDecimal etc = parseBigDecimal(record.get(3));
                    BigDecimal actuals = parseBigDecimal(record.get(4));

                    Assignment assignment = new Assignment(assignmentId, taskId, resourceId, etc, actuals);
                    assignments.add(assignment);

                } catch (Exception e) {
                    logger.error("Error processing line {}: {}", lineNumber, e.getMessage());
                }
            }

        } catch (IOException e) {
            logger.error("Error reading the input stream: {}", e.getMessage());
        }

        logger.info("Finished reading CSV file. Attempting to validate {} assignments.", assignments.size());
        assignmentService.validateAssignments(assignments);
    }
}

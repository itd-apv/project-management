package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

public abstract class BaseController {
    private static final Logger logger = LogManager.getLogger(BaseController.class);
    protected Integer parseInteger(String value) {
        if (value == null) {
            logger.error("Invalid Integer: value is null");
            return null;
        }
        value = value.trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.error("Invalid Integer value: {}", value);
            return null;
        }
    }

    protected Boolean parseBoolean(String value) {
        if (value == null || value.trim().isEmpty()) {
            logger.error("Invalid Boolean value: {}", value);
            return null;
        }
        value = value.trim();
        if (value.equalsIgnoreCase("false")) {
            return false;
        } else if (value.equalsIgnoreCase("true")) {
            return true;
        }
        logger.error("Invalid Boolean value: {}", value);
        return null;
    }

    protected String parseString(String value) {
        if (value == null || value.trim().isEmpty()) {
            logger.error("Invalid String: value is null or empty");
            return null;
        }
        value = value.trim();
        // Regular expression to check if the string contains only digits
        if (value.matches("\\d+")) {
            logger.error("Invalid String: value contains only digits - {}", value);
            return null;
        }
        return value;
    }

    protected Timestamp parseTimestamp(String value) {
        if (value == null || value.trim().isEmpty()) {
            logger.error("Invalid Timestamp: {}", value);
            return null;
        }
        try {
            return Timestamp.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Timestamp format: {}", value);
            return null;
        }
    }

    protected BigDecimal parseBigDecimal(String value) {
        if (value == null) {
            logger.error("Invalid BigDecimal: value is null");
            return null;
        }
        value = value.trim();
        try {

            BigDecimal parsedValue = new BigDecimal(value);
            // Splitting the string to check the number of digits before the decimal point
            String[] parts = value.split("\\.");
            String integerPart = parts[0];
            // Checking whether the integer part has more than 8 digits
            if (integerPart.length() > 8) {
                logger.error("Value has more than 8 digits before the decimal point: {}", value);
                return null;
            }
            if (parsedValue.precision() > 10 || parsedValue.scale() > 2) {
                logger.error("Value exceeds the limit of 10 total digits or more than 2 decimal places: {}", value);
                return null;
            }
            // Setting the scale and return the parsed value
            return parsedValue.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            logger.error("Invalid BigDecimal value: {}", value);
            return null;
        }
    }
}
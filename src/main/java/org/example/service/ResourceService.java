package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.ResourceDao;
import org.example.model.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceService {
    private static final Logger logger = LogManager.getLogger(ResourceService.class);
    private ResourceDao resourceDao;

    public ResourceService(ResourceDao resourceDao) {
        this.resourceDao = resourceDao;
    }

    public void validateResources(List<Resource> resources) throws SQLException {
        List<Resource> validResources = new ArrayList<>();
        Set<Integer> resourceIdSet = new HashSet<>();
        int lineNumber = 1;

        for (Resource resource : resources) {
            if (resource == null) {
                logger.warn("Skipping null resource on line {}", lineNumber);
                lineNumber++;
                continue;
            }

            if (isResourceValid(resource, resourceIdSet, lineNumber)) {
                validResources.add(resource);
            } else {
                logger.debug("Resource failed validation: {}", resource);
            }
            lineNumber++;
        }
        // Inserting valid resources
        if (!validResources.isEmpty()) {
            resourceDao.insertResources(validResources);
            logger.info("Successfully inserted {} valid resources into the database.", validResources.size());
        } else {
            logger.warn("No valid resources to insert.");
        }
    }

    private boolean isResourceValid(Resource resource, Set<Integer> resourceIdSet, int lineNumber) {
        return isValidResourceId(resource, resourceIdSet, lineNumber) &&
                isResourceNameValid(resource, lineNumber) &&
                isResourceIsActiveValid(resource, lineNumber);
    }

    private boolean isValidResourceId(Resource resource, Set<Integer> resourceIdSet, int lineNumber) {
        if (resource.getResourceId() <= 0) {
            logger.error("Invalid Resource ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        if (resourceIdSet.contains(resource.getResourceId())) {
            logger.error("Duplicate Resource ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        resourceIdSet.add(resource.getResourceId());
        return true;
    }

    private boolean isResourceNameValid(Resource resource, int lineNumber) {
        if (resource.getResourceName() == null || resource.getResourceName().trim().isEmpty()) {
            logger.error("Invalid Resource Name on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isResourceIsActiveValid(Resource resource, int lineNumber) {
        if (resource.getResourceIsActive() == null) {
            logger.error("Invalid Is Active on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }
}

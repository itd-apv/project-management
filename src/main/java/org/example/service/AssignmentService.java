package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.AssignmentDao;
import org.example.dao.ResourceDao;
import org.example.dao.TaskDao;
import org.example.model.Assignment;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssignmentService {
    private static final Logger logger = LogManager.getLogger(AssignmentService.class);
    private AssignmentDao assignmentDao;
    private ResourceDao resourceDao;
    private TaskDao taskDao;

    public AssignmentService(AssignmentDao assignmentDao, ResourceDao resourceDao, TaskDao taskDao) {
        this.assignmentDao = assignmentDao;
        this.resourceDao = resourceDao;
        this.taskDao = taskDao;
    }

    public void validateAssignments(List<Assignment> assignments) throws SQLException {
        List<Assignment> validAssignments = new ArrayList<>();
        Set<Integer> assignmentIdSet = new HashSet<>(); // Track unique assignment IDs
        int lineNumber = 1;

        for (Assignment assignment : assignments) {
            if (assignment == null) {
                logger.warn("Skipping null assignment on line {}", lineNumber);
                lineNumber++;
                continue;
            }

            if (isAssignmentValid(assignment, assignmentIdSet, lineNumber)) {
                validAssignments.add(assignment);
            } else {
                logger.debug("Assignment failed validation: {}", assignment);
            }
            lineNumber++;
        }

        if (!validAssignments.isEmpty()) {
            assignmentDao.insertAssignments(validAssignments);
            logger.info("Successfully inserted {} valid assignments into the database.", validAssignments.size());
        } else {
            logger.warn("No valid assignments to insert.");
        }
    }

    private boolean isAssignmentValid(Assignment assignment, Set<Integer> assignmentIdSet, int lineNumber) throws SQLException {
        return isAssignmentIdValid(assignment, assignmentIdSet, lineNumber) &&
                isTaskIdValid(assignment, lineNumber) &&
                isResourceIdValid(assignment, lineNumber) &&
                isEtcValid(assignment, lineNumber) &&
                isActualsValid(assignment, lineNumber) &&
                doTaskAndResourceExist(assignment, lineNumber);
    }

    private boolean isAssignmentIdValid(Assignment assignment, Set<Integer> assignmentIdSet, int lineNumber) {
        if (assignment.getAssignmentId() <= 0) {
            logger.error("Invalid Assignment ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        if (assignmentIdSet.contains(assignment.getAssignmentId())) {
            logger.error("Duplicate Assignment ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        assignmentIdSet.add(assignment.getAssignmentId());
        return true;
    }

    private boolean isTaskIdValid(Assignment assignment, int lineNumber) {
        if (assignment.getTaskId() <= 0) {
            logger.error("Invalid Task ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isResourceIdValid(Assignment assignment, int lineNumber) {
        if (assignment.getResourceId() <= 0) {
            logger.error("Invalid Resource ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isEtcValid(Assignment assignment, int lineNumber) {
        if (assignment.getEtc() == null) {
            logger.error("Invalid Etc on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isActualsValid(Assignment assignment, int lineNumber) {
        if (assignment.getActuals() == null) {
            logger.error("Invalid Actuals on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean doTaskAndResourceExist(Assignment assignment, int lineNumber) throws SQLException {
        if (!taskDao.doesTaskExist(assignment.getTaskId())) {
            logger.error("Invalid TaskId: {}", assignment.getTaskId());
            logger.error("Skipping invalid assignment on line {} - {} Invalid field: taskId (not found in task table)", lineNumber, getAssignmentDetails(assignment));
            return false;
        }

        if (!resourceDao.doesResourceExist(assignment.getResourceId())) {
            logger.error("Invalid ResourceId: {}", assignment.getResourceId());
            logger.error("Skipping invalid assignment on line {} - {} Invalid field: resourceId (not found in resource table)", lineNumber, getAssignmentDetails(assignment));
            return false;
        }

        return true;
    }

    private String getAssignmentDetails(Assignment assignment) {
        return "Assignment ID: " + assignment.getAssignmentId();
    }
}

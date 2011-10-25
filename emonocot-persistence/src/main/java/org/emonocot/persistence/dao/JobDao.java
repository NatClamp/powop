package org.emonocot.persistence.dao;

import java.util.List;

import org.olap4j.CellSet;
import org.springframework.batch.core.JobExecution;

/**
 *
 * @author ben
 *
 */
public interface JobDao {

    /**
     *
     * @param authorityName the name of the authorty
     * @param pageSize set the maximum size of the list of executions
     * @return a list of job executions
     */
    List<JobExecution> getJobExecutions(String authorityName, Integer pageSize);

    /**
     *
     * @param jobExecutionId Set the job execution identifier
     * @return a result object
     */
    CellSet countObjects(Long jobExecutionId);

    /**
     *
     * @param jobExecutionId Set the job execution identifier
     * @param objectType set the object type
     * @return a result object
     */
    CellSet countErrors(Long jobExecutionId, String objectType);

}

package org.emonocot.portal.integration;

import org.emonocot.api.JobService;
import org.emonocot.api.job.JobExecutionInfo;
import org.emonocot.api.job.JobStatusNotifier;
import org.emonocot.model.job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ben
 *
 */
@Service
public class JobStatusNotifierImpl implements JobStatusNotifier {
    /**
    *
    */
    private static Logger logger = LoggerFactory
            .getLogger(JobStatusNotifierImpl.class);

    /**
    *
    */
    private JobService service;

    /**
     * @param newJobService
     *            the jobService to set
     */
    @Autowired
    public final void setJobService(final JobService newJobService) {
        this.service = newJobService;
    }

    /**
     * @param jobExecutionInfo
     *            Set the job execution info
     */
    public final void notify(final JobExecutionInfo jobExecutionInfo) {
        logger.debug("In notify " + jobExecutionInfo.getId());

        Job job = service.findByJobId(jobExecutionInfo.getId());
        job.setDuration(jobExecutionInfo.getDuration());
        job.setExitCode(jobExecutionInfo.getExitCode());
        job.setExitDescription(jobExecutionInfo.getExitDescription());
        if (jobExecutionInfo.getJobInstance() != null) {
            job.setJobInstance(jobExecutionInfo.getJobInstance().getResource());
        }
        job.setResource(jobExecutionInfo.getResource());
        job.setStartTime(jobExecutionInfo.getStartTime());
        job.setStatus(jobExecutionInfo.getStatus());

        service.saveOrUpdate(job);
        logger.debug("Returning");
    }

}
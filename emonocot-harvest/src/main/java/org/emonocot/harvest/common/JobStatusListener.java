/*
 * This is eMonocot, a global online biodiversity information resource.
 *
 * Copyright © 2011–2015 The Board of Trustees of the Royal Botanic Gardens, Kew and The University of Oxford
 *
 * eMonocot is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * eMonocot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * The complete text of the GNU Affero General Public License is in the source repository as the file
 * ‘COPYING’.  It is also available from <http://www.gnu.org/licenses/>.
 */
package org.emonocot.harvest.common;

import org.emonocot.api.job.JobExecutionInfo;
import org.emonocot.api.job.JobStatusNotifier;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

/**
 *
 * @author ben
 *
 */
public class JobStatusListener extends JobExecutionListenerSupport {

	private JobStatusNotifier jobStatusNotifier;

	private String baseUrl;

	public void setBaseUrl(String newBaseUrl) {
		this.baseUrl = newBaseUrl;
	}

	public void setJobStatusNotifier(
			JobStatusNotifier newJobStatusNotifier) {
		this.jobStatusNotifier = newJobStatusNotifier;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		this.notify(jobExecution);
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		this.notify(jobExecution);
	}

	private void notify(JobExecution jobExecution) {
		if (jobExecution.getJobInstance().getJobParameters().getString("resource.identifier") != null) {
			JobExecutionInfo jobExecutionInfo = new JobExecutionInfo(jobExecution, baseUrl);
			jobStatusNotifier.notify(jobExecutionInfo);
		}
	}

}

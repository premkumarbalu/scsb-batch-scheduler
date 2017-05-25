package org.main.recap.batch.service;

import org.main.recap.RecapConstants;
import org.main.recap.jpa.JobDetailsRepository;
import org.main.recap.model.jpa.JobEntity;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Created by akulak on 10/5/17.
 */
@Service
public class DailyReconcilationService {

    private static final Logger logger = LoggerFactory.getLogger(DailyReconcilationService.class);

    @Autowired
    private JobDetailsRepository jobDetailsRepository;

    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public JobDetailsRepository getJobDetailsRepository() {
        return jobDetailsRepository;
    }

    public String dailyReconcilation(String serverProtocol, String solrCircUrl, String jobName, Date lastExecutedTime) {
        String resultStatus = null;
        try {
            JobEntity jobEntity = getJobDetailsRepository().findByJobName(jobName);
            jobEntity.setLastExecutedTime(lastExecutedTime);
            CronExpression cronExpression = new CronExpression(jobEntity.getCronExpression());
            jobEntity.setNextRunTime(cronExpression.getNextValidTimeAfter(lastExecutedTime));
            HttpHeaders headers = new HttpHeaders();
            headers.set(RecapConstants.API_KEY, RecapConstants.RECAP);
            HttpEntity<JobEntity> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = getRestTemplate().exchange(serverProtocol + solrCircUrl +RecapConstants.DAILY_RECONCILATION_URL, HttpMethod.POST, httpEntity, String.class);
            resultStatus = responseEntity.getBody();
        } catch (Exception ex) {
            logger.error(RecapConstants.LOG_ERROR, ex);
            resultStatus = ex.getMessage();
        }
        return resultStatus;
    }
}
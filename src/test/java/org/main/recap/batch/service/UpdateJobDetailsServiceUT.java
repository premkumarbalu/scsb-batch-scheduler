package org.main.recap.batch.service;

import org.junit.Test;
import org.main.recap.BaseTestCase;
import org.main.recap.RecapConstants;
import org.main.recap.jpa.JobDetailsRepository;
import org.main.recap.model.jpa.JobEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 19/4/17.
 */
public class UpdateJobDetailsServiceUT extends BaseTestCase {

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.solr.client.url}")
    String solrClientUrl;

    @Mock
    RestTemplate restTemplate;

    @Mock
    JobDetailsRepository jobDetailsRepository;

    @Mock
    UpdateJobDetailsService updateJobDetailsService;

    @Test
    public void testUpdateJobDetailsService() {
        String jobName  = RecapConstants.PURGE_EXCEPTION_REQUESTS;
        Date lastExecutedTime = new Date();
        JobEntity jobEntity = new JobEntity();
        jobEntity.setJobName(jobName);
        jobEntity.setCronExpression("0/10 * * * * ? *");

        HttpHeaders headers = new HttpHeaders();
        headers.set(RecapConstants.API_KEY, RecapConstants.RECAP);
        HttpEntity<JobEntity> httpEntity = new HttpEntity<>(jobEntity, headers);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(RecapConstants.SUCCESS, HttpStatus.OK);
        Mockito.when(updateJobDetailsService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(updateJobDetailsService.getJobDetailsRepository()).thenReturn(jobDetailsRepository);
        Mockito.when(updateJobDetailsService.getJobDetailsRepository().findByJobName(jobName)).thenReturn(jobEntity);
        Mockito.when(updateJobDetailsService.getRestTemplate().exchange(serverProtocol + solrClientUrl + RecapConstants.UPDATE_JOB_URL, HttpMethod.POST, httpEntity, String.class)).thenReturn(responseEntity);
        Mockito.when(updateJobDetailsService.updateJob(serverProtocol, solrClientUrl, jobName, lastExecutedTime)).thenCallRealMethod();
        String status = updateJobDetailsService.updateJob(serverProtocol, solrClientUrl, jobName, lastExecutedTime);
        assertNotNull(status);
        assertEquals(status, RecapConstants.SUCCESS);
    }
}
package fr.kuremento.dnd.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Lancement du job '{}'", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Fin du job '{}' en {} sec",
                 jobExecution.getJobInstance().getJobName(),
                 Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).getSeconds());
    }
}

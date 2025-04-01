package fr.kuremento.dnd.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
public class StepCompletionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Lancement de la step '{}'", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (ExitStatus.FAILED.equals(stepExecution.getExitStatus())) {
            log.error("Fin de la step '{}' en erreur : {}",
                      stepExecution.getStepName(),
                      stepExecution.getExitStatus().getExitDescription());
        } else {
            if (StringUtils.isNotBlank(stepExecution.getExitStatus().getExitDescription())) {
                log.info("Fin de la step '{}' en {} sec : {}",
                         stepExecution.getStepName(),
                         Duration.between(stepExecution.getStartTime(),
                                          Objects.isNull(stepExecution.getEndTime()) ? LocalDateTime.now() : stepExecution.getEndTime())
                                 .getSeconds(),
                         stepExecution.getExitStatus().getExitDescription());
            } else {
                log.info("Fin de la step '{}' en {} sec",
                         stepExecution.getStepName(),
                         Duration.between(stepExecution.getStartTime(),
                                          Objects.isNull(stepExecution.getEndTime()) ? LocalDateTime.now() : stepExecution.getEndTime())
                                 .getSeconds());
            }

        }
        return stepExecution.getExitStatus();
    }
}

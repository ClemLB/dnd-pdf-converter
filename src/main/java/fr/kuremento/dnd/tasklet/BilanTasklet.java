package fr.kuremento.dnd.tasklet;

import fr.kuremento.dnd.model.Constantes;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

/**
 * Service de bilan pour de la supervision
 */
@Slf4j
@Component("bilanTasklet")
public class BilanTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, ChunkContext chunkContext) {

        ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();

        log.info("**************************************************************************");
        log.info("Nombre de lignes lues : {}", executionContext.getLong(Constantes.JobContext.NB_LIGNES_LUES, 0));
        log.info("**************************************************************************");
        return RepeatStatus.FINISHED;
    }
}

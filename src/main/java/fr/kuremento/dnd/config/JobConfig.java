package fr.kuremento.dnd.config;

import fr.kuremento.dnd.listener.JobCompletionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobConfig extends DefaultBatchConfiguration {

    private final JobRepository jobRepository;
    private final DataSourceTransactionManager transactionManager;

    /**
     * Job :
     * <ol>
     * <li>bilan du traitement</li>
     * </ol>
     *
     * @param listener  listener commun aux jobs
     * @param bilanStep étape de bilan pour de la supervision
     */
    @Bean
    public Job job(JobCompletionListener listener, @Qualifier("bilanStep") Step bilanStep) {
        return new JobBuilder("traitement", jobRepository).listener(listener).start(bilanStep).build();
    }

    @Bean("bilanStep")
    public Step bilan(@Qualifier("bilanTasklet") Tasklet tasklet) {
        return new StepBuilder("bilan", jobRepository).tasklet(tasklet, transactionManager).build();
    }
}

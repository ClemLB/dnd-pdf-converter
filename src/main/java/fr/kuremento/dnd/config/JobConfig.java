package fr.kuremento.dnd.config;

import fr.kuremento.dnd.listener.JobCompletionListener;
import fr.kuremento.dnd.listener.StepCompletionListener;
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
     * <li>traitement du fichier PDF</li>
     * <li>bilan du traitement</li>
     * </ol>
     *
     * @param listener    listener commun aux jobs
     * @param readPdfStep étape de traitement du fichier PDF d'entrée
     * @param bilanStep   étape de bilan pour de la supervision
     */
    @Bean
    public Job job(JobCompletionListener listener,
                   @Qualifier("readPdfStep") Step readPdfStep,
                   @Qualifier("convertCategoriesStep") Step convertCategoriesStep,
                   @Qualifier("writePdfStep") Step writePdfStep,
                   @Qualifier("bilanStep") Step bilanStep) {
        return new JobBuilder("traitement", jobRepository).listener(listener)
                                                          .start(readPdfStep)
                                                          .next(convertCategoriesStep)
                                                          .next(writePdfStep)
                                                          .next(bilanStep)
                                                          .build();
    }

    @Bean("readPdfStep")
    public Step readPdf(StepCompletionListener listener, @Qualifier("readPdfTasklet") Tasklet tasklet) {
        return new StepBuilder("lecture du pdf anglais", jobRepository).listener(listener).tasklet(tasklet, transactionManager).build();
    }

    @Bean("convertCategoriesStep")
    public Step convertCategories(StepCompletionListener listener, @Qualifier("convertCategoriesTasklet") Tasklet tasklet) {
        return new StepBuilder("conversion des catégories", jobRepository).listener(listener).tasklet(tasklet, transactionManager).build();
    }

    @Bean("writePdfStep")
    public Step writePdf(StepCompletionListener listener, @Qualifier("writePdfTasklet") Tasklet tasklet) {
        return new StepBuilder("écriture du pdf français", jobRepository).listener(listener).tasklet(tasklet, transactionManager).build();
    }

    @Bean("bilanStep")
    public Step bilan(@Qualifier("bilanTasklet") Tasklet tasklet) {
        return new StepBuilder("bilan", jobRepository).tasklet(tasklet, transactionManager).build();
    }
}

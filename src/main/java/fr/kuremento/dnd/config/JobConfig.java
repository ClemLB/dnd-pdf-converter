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
     * <li>Lecture du fichier PDF</li>
     * <li>Mapping des catégories</li>
     * <li>Écriture d'un nouveau fichier PDF</li>
     * </ol>
     *
     * @param listener              listener commun aux jobs
     * @param readPdfStep           étape de lecture du fichier PDF d'entrée
     * @param convertCategoriesStep étape de traitement des catégories
     * @param writePdfStep          étape d'écriture du fichier PDF de sortie
     */
    @Bean
    public Job job(JobCompletionListener listener,
                   @Qualifier("readPdfStep") Step readPdfStep,
                   @Qualifier("convertCategoriesStep") Step convertCategoriesStep,
                   @Qualifier("translationStep") Step translationStep,
                   @Qualifier("writePdfStep") Step writePdfStep) {
        return new JobBuilder("traitement", jobRepository).listener(listener)
                                                          .start(readPdfStep)
                                                          .next(convertCategoriesStep)
                                                          .next(translationStep)
                                                          .next(writePdfStep)
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

    @Bean("translationStep")
    public Step translate(StepCompletionListener listener, @Qualifier("translateTasklet") Tasklet tasklet) {
        return new StepBuilder("traduction", jobRepository).listener(listener).tasklet(tasklet, transactionManager).build();
    }

    @Bean("writePdfStep")
    public Step writePdf(StepCompletionListener listener, @Qualifier("writePdfTasklet") Tasklet tasklet) {
        return new StepBuilder("écriture du pdf français", jobRepository).listener(listener).tasklet(tasklet, transactionManager).build();
    }

}

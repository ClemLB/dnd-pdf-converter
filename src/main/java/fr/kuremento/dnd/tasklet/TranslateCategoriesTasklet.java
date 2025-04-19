package fr.kuremento.dnd.tasklet;

import fr.kuremento.dnd.model.Constantes;
import fr.kuremento.dnd.model.FichePersonnageCategorie;
import fr.kuremento.dnd.model.data.DataMappingFrench;
import fr.kuremento.dnd.repository.MappingFrenchRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service de traduction des catégories
 */
@Slf4j
@RequiredArgsConstructor
@Component("translateCategoriesTasklet")
public class TranslateCategoriesTasklet implements Tasklet, StepExecutionListener {

    private final MappingFrenchRepository mappingFrenchRepository;
    private List<FichePersonnageCategorie> categories;

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, ChunkContext chunkContext) {
        List<FichePersonnageCategorie> list = (List<FichePersonnageCategorie>) chunkContext.getStepContext()
                                                                                           .getJobExecutionContext()
                                                                                           .get(Constantes.JobContext.CATEGORIES);
        categories = list.stream().flatMap(this::mapCategory).toList();
        return RepeatStatus.FINISHED;
    }

    private Stream<FichePersonnageCategorie> mapCategory(FichePersonnageCategorie categorie) {
        List<DataMappingFrench> dataMappingFrenches = mappingFrenchRepository.findByEnglishCategory(categorie.categoryName());
        if (dataMappingFrenches.isEmpty()) {
            return Stream.of(categorie);
        }
        return dataMappingFrenches.stream()
                                  .map(dataMappingFrench -> new FichePersonnageCategorie(dataMappingFrench.getFrenchCategory(), categorie.categoryValue(),
                                                                                         dataMappingFrench.isCheckBox()));
    }

    @Override
    @AfterStep
    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
        if (!categories.isEmpty()) {
            ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
            jobContext.put(Constantes.JobContext.CATEGORIES, categories);
            return ExitStatus.COMPLETED;
        } else {
            return ExitStatus.FAILED.addExitDescription("Erreur lors de la conversion du nom des catégories");
        }
    }
}

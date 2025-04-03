package fr.kuremento.dnd.tasklet;

import fr.kuremento.dnd.model.Constantes;
import fr.kuremento.dnd.model.FichePersonnageCategorie;
import fr.kuremento.dnd.model.data.DataTranslation;
import fr.kuremento.dnd.repository.TranslationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
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
import java.util.stream.Collectors;

/**
 * Service de traduction
 */
@Slf4j
@RequiredArgsConstructor
@Component("translateTasklet")
public class TranslateTasklet implements Tasklet, StepExecutionListener {

    private final TranslationRepository translationRepository;
    private List<FichePersonnageCategorie> categories;

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, ChunkContext chunkContext) {
        var list = (List<FichePersonnageCategorie>) chunkContext.getStepContext().getJobExecutionContext().get(Constantes.JobContext.CATEGORIES);
        categories = list.stream().map(this::translate).toList();
        return RepeatStatus.FINISHED;
    }

    private FichePersonnageCategorie translate(FichePersonnageCategorie categorie) {
        List<DataTranslation> data = translationRepository.findByMapping_FrenchCategory(categorie.categoryName());
        if (data.isEmpty()) {
            return categorie;
        }
        if (log.isDebugEnabled()) {
            log.debug("{} : {}", categorie.categoryName(),
                      data.stream().map(translation -> translation.getEnglishValue() + "/" + translation.getFrenchValue()).collect(Collectors.joining(", ")));
        }
        String translatedValue = categorie.categoryValue();
        for (DataTranslation translation : data) {
            translatedValue = RegExUtils.replaceAll(translatedValue, "\\b" + translation.getEnglishValue() + "\\b", translation.getFrenchValue());
        }
        return new FichePersonnageCategorie(categorie.categoryName(), translatedValue, categorie.isCheckBox());
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

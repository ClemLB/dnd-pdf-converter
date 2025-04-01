package fr.kuremento.dnd.tasklet;

import fr.kuremento.dnd.model.Constantes;
import fr.kuremento.dnd.model.FichePersonnageCategorie;
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

import java.io.IOException;
import java.util.List;

/**
 * Service de lecture d'un fichier PDF
 */
@Slf4j
@Component("convertCategoriesTasklet")
@RequiredArgsConstructor
public class ConvertCategoriesTasklet implements Tasklet, StepExecutionListener {

    private List<FichePersonnageCategorie> categories;

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, ChunkContext chunkContext) {
        List<FichePersonnageCategorie> list = (List<FichePersonnageCategorie>) chunkContext.getStepContext()
                                                                                           .getJobExecutionContext()
                                                                                           .get(Constantes.JobContext.CATEGORIES);
        categories = list.stream().map(this::mapCategories).toList();
        return RepeatStatus.FINISHED;
    }

    private FichePersonnageCategorie mapCategories(FichePersonnageCategorie fichePersonnageCategorie) {
        String categoryName = fichePersonnageCategorie.categoryName();
        String newCategoryName = switch (categoryName) {
            case "CLASS  LEVEL" -> "ClassLevel";
            case "PLAYER NAME" -> "PlayerName";
            case "RACE" -> "Race ";
            case "BACKGROUND" -> "Background";
            case "EXPERIENCE POINTS" -> "XP";
            case "CHamod" -> "CHAmod";
            case "Acrobatics" -> "Acrobaties";
            case "MaxHP" -> "HPMax";
            case "CurrentHP" -> "HPCurrent";
            case "TempHP" -> "HPTemp";
            case "Arcana" -> "Arcanes";
            case "Athletics" -> "Athlétisme";
            case "Animal" -> "Dressage";
            case "Stealth " -> "Discrétion";
            case "SleightofHand" -> "Escamotage";
            case "History" -> "Histoire";
            case "Insight" -> "Intuition";
            case "Medicine" -> "Médecine";
            case "Performance" -> "Représentation";
            case "Deception" -> "Tromperie";
            case "Survival" -> "Survie";
            case "Init" -> "Initiative";
            default -> categoryName;
        };
        return new FichePersonnageCategorie(newCategoryName, fichePersonnageCategorie.categoryValue());
    }

    @Override
    @AfterStep
    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
        if (!categories.isEmpty()) {
            ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
            jobContext.put(Constantes.JobContext.CATEGORIES, categories);
            log.debug("Mise à jour de {} catégories", categories.size());
            return ExitStatus.COMPLETED;
        } else {
            return ExitStatus.FAILED.addExitDescription("Erreur lors de la lecture du PDF");
        }
    }
}

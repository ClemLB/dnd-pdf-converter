package fr.kuremento.dnd.tasklet;

import fr.kuremento.dnd.model.Constantes;
import fr.kuremento.dnd.model.FichePersonnageCategorie;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de lecture d'un fichier PDF
 */
@Slf4j
@Component("readPdfTasklet")
@RequiredArgsConstructor
public class ReadPdfTasklet implements Tasklet, StepExecutionListener {

    private List<FichePersonnageCategorie> categories;

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, ChunkContext chunkContext) throws IOException {
        File inputFile = new File(String.valueOf(chunkContext.getStepContext().getJobParameters().get(Constantes.JobParameters.INPUT_FILE)));
        PDDocument document = Loader.loadPDF(inputFile);
        if (!document.isEncrypted()) {
            categories = new ArrayList<>();
            for (PDPage page : document.getPages()) {
                page.getAnnotations().stream().map(PDAnnotation::getCOSObject).forEach(dictionary -> {
                    String categoryName = dictionary.getString("T");
                    String categoryValue = dictionary.getString("V");
                    categories.add(new FichePersonnageCategorie(categoryName, categoryValue, false));
                });
            }
        }
        document.close();
        return RepeatStatus.FINISHED;
    }

    @Override
    @AfterStep
    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
        if (!categories.isEmpty()) {
            ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
            jobContext.put(Constantes.JobContext.CATEGORIES, categories);
            return ExitStatus.COMPLETED;
        } else {
            return ExitStatus.FAILED.addExitDescription("Erreur lors de la lecture du PDF");
        }
    }
}

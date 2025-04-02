package fr.kuremento.dnd.tasklet;

import fr.kuremento.dnd.model.Constantes;
import fr.kuremento.dnd.model.FichePersonnageCategorie;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Service de lecture d'un fichier PDF
 */
@Slf4j
@Component("writePdfTasklet")
@RequiredArgsConstructor
public class WritePdfTasklet implements Tasklet {

    @Value("${application.file.input-empty-file}")
    private final Resource inputEmptyFile;

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, ChunkContext chunkContext) throws IOException {
        List<FichePersonnageCategorie> categories = (List<FichePersonnageCategorie>) chunkContext.getStepContext()
                                                                                                 .getJobExecutionContext()
                                                                                                 .get(Constantes.JobContext.CATEGORIES);
        PDDocument document = Loader.loadPDF(inputEmptyFile.getContentAsByteArray());
        for (PDPage page : document.getPages()) {
            page.getAnnotations().stream().map(PDAnnotation::getCOSObject).forEach(dictionary -> {
                String categoryName = dictionary.getString("T");
                FichePersonnageCategorie category = categories.stream()
                                                              .filter(lambdaCategory -> categoryName.equals(lambdaCategory.categoryName()))
                                                              .findAny()
                                                              .orElse(new FichePersonnageCategorie(categoryName, "", false));
                if (!categories.stream().map(FichePersonnageCategorie::categoryName).toList().contains(categoryName)) {
                    log.warn("{}", categoryName);
                }
                if (StringUtils.isNotBlank(category.categoryValue())) {
                    if (category.isCheckBox()) {
                        dictionary.setItem(COSName.AS, COSName.YES);
                    } else {
                        dictionary.setItem(COSName.V, new COSString(category.categoryValue()));
                    }
                }

            });
        }

        File outputFile = new File(
                new File(String.valueOf(chunkContext.getStepContext().getJobParameters().get(Constantes.JobParameters.INPUT_FILE))).getParentFile(),
                "Fiche de personnage.pdf");

        document.save(outputFile);
        document.close();
        return RepeatStatus.FINISHED;
    }
}

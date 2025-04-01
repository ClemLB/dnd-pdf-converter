package fr.kuremento.dnd.tasklet;

import fr.kuremento.dnd.model.Constantes;
import fr.kuremento.dnd.model.FichePersonnageCategorie;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
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

    @Value("${application.file.output-file}")
    private final Resource outputFile;

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, ChunkContext chunkContext) throws IOException {
        List<FichePersonnageCategorie> categories = (List<FichePersonnageCategorie>) chunkContext.getStepContext()
                                                                                                 .getJobExecutionContext()
                                                                                                 .get(Constantes.JobContext.CATEGORIES);
        PDDocument document = Loader.loadPDF(outputFile.getFile().getAbsoluteFile());
        for (PDPage page : document.getPages()) {
            page.getAnnotations().stream().map(PDAnnotation::getCOSObject).forEach(dictionary -> {
                String categoryName = dictionary.getString("T");
                String categoryValue = categories.stream()
                                                 .filter(category -> categoryName.equals(category.categoryName()))
                                                 .findAny()
                                                 .orElse(new FichePersonnageCategorie(categoryName, ""))
                                                 .categoryValue();
                if(!categories.stream().map(FichePersonnageCategorie::categoryName).toList().contains(categoryName)) {
                    log.warn("{}", categoryName);
                }
                if (StringUtils.isNotBlank(categoryValue)) {
                    dictionary.setItem("V", new COSString(categoryValue));
                }
            });
        }
        document.save(new File("output.pdf"));
        document.close();
        return RepeatStatus.FINISHED;
    }
}

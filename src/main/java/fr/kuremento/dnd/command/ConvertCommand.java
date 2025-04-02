package fr.kuremento.dnd.command;

import fr.kuremento.dnd.model.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.util.Optional;

@Command
@RequiredArgsConstructor
public class ConvertCommand {

    private final Job job;
    private final JobLauncher jobLauncher;

    @Command(command = "convert", description = "Convert an english PDF from dndbeyond.com to a french one", alias = "c")
    public void convert(@Option(longNames = "input-file", description = "Chemin vers la fiche de personnage anglaise", required = true) String inputFile,
                        @Option(longNames = "output-directory", description = "Chemin du dossier du PDF français généré en sortie") String outputFile) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        var jobParametersBuilder = new JobParametersBuilder().addString(Constantes.JobParameters.ID, String.valueOf(System.currentTimeMillis()))
                                                             .addString(Constantes.JobParameters.INPUT_FILE, inputFile);
        if (Optional.ofNullable(outputFile).isPresent()) {
            jobParametersBuilder = jobParametersBuilder.addString(Constantes.JobParameters.OUTPUT_FILE, outputFile);
        }
        jobLauncher.run(job, jobParametersBuilder.toJobParameters());
    }
}

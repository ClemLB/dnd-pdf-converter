package fr.kuremento.dnd.command;

import fr.kuremento.dnd.model.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command
@RequiredArgsConstructor
public class ConvertCommand {

    private final Job job;
    private final JobLauncher jobLauncher;

    @Command(command = "convert", description = "Convert an english PDF from dndbeyond.com to a french one", alias = "c")
    public String convert(@Option(longNames = "input-file", description = "Chemin vers la fiche de personnage anglaise", required = true) String inputFile) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        var jobParametersBuilder = new JobParametersBuilder().addString(Constantes.JobParameters.ID, String.valueOf(System.currentTimeMillis()))
                                                             .addString(Constantes.JobParameters.INPUT_FILE, inputFile);
        var jobExecution = jobLauncher.run(job, jobParametersBuilder.toJobParameters());
        if (!ExitStatus.COMPLETED.getExitCode().equals(jobExecution.getExitStatus().getExitCode())) {
            return "Une erreur est survenue lors de la conversion : " + jobExecution.getAllFailureExceptions();
        } else {
            return "Le PDF " + inputFile + " a bien été converti.";
        }
    }
}

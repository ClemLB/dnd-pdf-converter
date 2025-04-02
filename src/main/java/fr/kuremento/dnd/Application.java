package fr.kuremento.dnd;

import fr.kuremento.dnd.command.ConvertCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.EnableCommand;

@Slf4j
@SpringBootApplication
@EnableCommand(ConvertCommand.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

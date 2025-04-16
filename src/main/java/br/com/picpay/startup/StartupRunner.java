package br.com.picpay.startup;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Log4j2
@Component
public class StartupRunner {

    private final String sqsCommandEmailQueue = """
            aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name picpay-notification-email-queue --region us-east-1""";

    private final String sqsCommandSmsQueue = """
            aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name picpay-notification-sms-queue --region us-east-1""";

    private final String sesCommand = """
            aws --endpoint-url=http://localhost:4566 ses verify-email-identity --email-address from@example.com --region us-east-1""";

    private final String snsCommand = """
            aws --endpoint-url=http://localhost:4566 sns create-topic --name notification-sms-topic --region us-east-1""";


    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting application...");
        executeCommand(sqsCommandEmailQueue);
        executeCommand(sqsCommandSmsQueue);
        executeCommand(sesCommand);
        executeCommand(snsCommand);
        log.info("Application started successfully!");
    }

    private void executeCommand(String command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("bash", "-c", command);
        }

        try {
            Process process = processBuilder.start();

            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(System.out::println);
            }

            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                reader.lines().forEach(System.err::println);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Error executing command: " + command);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Error executing command: " + command, e);
        }
    }
}

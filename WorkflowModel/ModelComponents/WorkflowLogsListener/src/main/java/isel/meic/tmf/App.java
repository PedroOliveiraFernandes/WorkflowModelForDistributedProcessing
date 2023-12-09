package isel.meic.tmf;

import com.rabbitmq.client.ConnectionFactory;
import isel.meic.tmf.arguments.ArgsMapper;
import isel.meic.tmf.arguments.ArgumentsValidator;
import isel.meic.tmf.arguments.LogListenerArguments;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class App {
    public static void main(String[] args) throws IOException, TimeoutException {
        var arguments = ArgsMapper.map(args);
        ArgumentsValidator.validate(arguments);

        listenToLogs(arguments);
    }

    private static void listenToLogs(LogListenerArguments arguments) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(arguments.broker.host);
        factory.setPort(arguments.broker.port);
        var connection = factory.newConnection();
        var channel = connection.createChannel();
        System.out.println("Starting log listening in " + arguments.broker.host + ":" + arguments.broker.port);

        channel.exchangeDeclare("logs", "fanout", true, false, null);
        var queue = channel.queueDeclare("", true, true, true, null);
        channel.queueBind(queue.getQueue(), "logs", "");

        BufferedWriter fileWriter;
        if (arguments.outputFile != null) {
            fileWriter = new BufferedWriter(new FileWriter(arguments.outputFile, false));
        } else {
            fileWriter = null;
        }

        channel.basicConsume(
                queue.getQueue(),
                true,
                (consumerTag, delivery) -> {
                    String recMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    if (arguments.filterByWorkflowName == null || arguments.filterByWorkflowName.isEmpty()) {
                        return;
                    }

                    System.out.println(recMessage);

                    // Append recMessage to the file if fileWriter is not null
                    if (fileWriter != null) {
                        try {
                            fileWriter.write(recMessage + "\n");
                            fileWriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                a -> {}
        );

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String input = reader.readLine();
            if (Objects.equals(input.toLowerCase(), "x")) break;
        }

        System.out.println("Finishing log listening...");

        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        channel.close();
        connection.close();
        System.out.println("Finished log listening.");
    }
}

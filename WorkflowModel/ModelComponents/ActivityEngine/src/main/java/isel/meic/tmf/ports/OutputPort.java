package isel.meic.tmf.ports;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import isel.meic.tmf.configuration.models.PortDto;
import isel.meic.tmf.engine.IterationController;
import isel.meic.tmf.token.TaskResult;
import isel.meic.tmf.token.Token;

import java.io.IOException;

public class OutputPort {
    private final PortDto output;
    private final Channel channel;

    public OutputPort(PortDto output, Channel channel) {

        this.output = output;
        this.channel = channel;
    }

    public void emit(Token token) throws IOException {
        var tokenMessage = new Gson().toJson(token)
                                     .getBytes();

        channel.queueDeclare(output.channel, true, false, false, null);
        channel.basicPublish("", output.channel, null, tokenMessage);
    }

    public String getName() {
        return output.name;
    }
}

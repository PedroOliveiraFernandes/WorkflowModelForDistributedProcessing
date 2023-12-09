package isel.meic.tmf.ports;

import isel.meic.tmf.configuration.models.PortDto;
import isel.meic.tmf.engine.IterationController;
import isel.meic.tmf.token.InputToken;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class InputPort {
    private final TokenReceptionCallback tokenReceptionCallback;
    private final PortDto portModel;
    private final BrokerInteraction brokerInteraction;

    public InputPort(PortDto portModel, BrokerInteraction brokerInteraction, IterationController iterationController) {
        this.portModel = portModel;
        this.brokerInteraction = brokerInteraction;
        this.tokenReceptionCallback = new TokenReceptionCallback(iterationController, brokerInteraction);
    }

    public void init() throws IOException {
        brokerInteraction.createQueue(portModel.channel, tokenReceptionCallback);
    }

    public CompletableFuture<InputToken> getTokenFuture() {
        return tokenReceptionCallback.getTokenFuture()
                                     .thenApply(tokenMessageDetails -> new InputToken(portModel.name,
                                                                                      tokenMessageDetails.token));
    }

    public void confirmToken(){
        try {
            brokerInteraction.acknowlege(tokenReceptionCallback.getTokenFuture().get().deliveryTag);
        } catch (Exception ignored) {
        }
    }

    @Override
    public String toString() {
        return portModel.toString();
    }
}

package isel.meic.tmf.ports;

import com.google.gson.Gson;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import isel.meic.tmf.engine.IterationController;
import isel.meic.tmf.token.Token;
import isel.meic.tmf.utils.ActivityLogger;
import isel.meic.tmf.utils.IObserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class TokenReceptionCallback implements DeliverCallback, IObserver {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    private final IterationController iterationController;
    private final BrokerInteraction brokerInteraction;
    private CompletableFuture<TokenMessageDetails> tokenFuture;
    private boolean canReceiveMessages;

    public TokenReceptionCallback(IterationController iterationController, BrokerInteraction brokerInteraction) {
        this.brokerInteraction = brokerInteraction;
        this.tokenFuture = new CompletableFuture<>();
        this.iterationController = iterationController;
        iterationController.subscribe(this);
        canReceiveMessages=true;
    }

    public CompletableFuture<TokenMessageDetails> getTokenFuture() {
        return tokenFuture;
    }

    @Override
    public void notifyChange() {
        tokenFuture = new CompletableFuture<>();
        canReceiveMessages=true;
    }

    @Override
    public void handle(String consumerTag, Delivery delivery) throws IOException {
        String recMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);

        TokenMessageDetails tokenMessageDetails = mapReceivedMessageToToken( delivery);
        if (!canReceiveMessages||!iterationController.isCurrentEqualTo(tokenMessageDetails.token.iteration)) {
            brokerInteraction.notAckknowlage(delivery.getEnvelope()
                                                     .getDeliveryTag());
            return;
        }
        canReceiveMessages=false;
        logMessageReception(delivery, recMessage);

        tokenFuture.complete(tokenMessageDetails);
    }

    private void logMessageReception(Delivery message, String recMessage) {
        String routingKey = message
                .getEnvelope()
                .getRoutingKey();
        logger.debug("Message Received from channel: " + routingKey + " and message content: " + recMessage);
    }

    private TokenMessageDetails mapReceivedMessageToToken(Delivery message) {
        String recMessage = new String(message.getBody(), StandardCharsets.UTF_8);

        return new TokenMessageDetails(new Gson().fromJson(recMessage, Token.class), message.getEnvelope()
                                                                                            .getDeliveryTag());
    }
}

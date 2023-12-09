package isel.meic.tmf.ports;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import isel.meic.tmf.configuration.models.BrokerInfoDto;
import isel.meic.tmf.utils.ActivityLogger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BrokerInteraction {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    private final BrokerInfoDto brokerInfo;
    private Connection connection;
    private Channel channel;

    public BrokerInteraction(BrokerInfoDto brokerInfo) {
        this.brokerInfo = brokerInfo;
    }

    public Channel getChannel() {
        return channel;
    }

    public void init() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(brokerInfo.host);
        factory.setPort(brokerInfo.port);
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            logger.debug("Can't connect to broker host: " + brokerInfo.host + " port: " + brokerInfo.port);
            throw e;
        }
    }

    public void close() throws IOException {
        try {
            channel.close();
        } catch (IOException | TimeoutException e) {
            logger.debug("Can't close channel to broker");
        }
        finally {
            connection.close();
        }
    }

    public void createQueue(String channelName, DeliverCallback deliverCallback) throws IOException {
        channel.queueDeclare(channelName, true, false, false, null);
        channel.basicConsume(
                channelName,
                false,
                deliverCallback,
                a -> {
                }
        );
    }

    public void notAckknowlage(long deliveryTag) throws IOException {
        channel.basicNack(deliveryTag, false, true);
    }

    public void acknowlege(long deliveryTag) throws IOException {
        channel.basicAck(deliveryTag, false);
    }

    public void createExchange(String exchangeName, String type) throws IOException {
        channel.exchangeDeclare(exchangeName, type, true, false, null);
    }

    public void publishToExchange(String exchangeName, String message) throws IOException {
        channel.basicPublish(exchangeName, "", null, message.getBytes());
    }
}
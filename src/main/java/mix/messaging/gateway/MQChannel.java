package mix.messaging.gateway;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * | Created by juleskreutzer
 * | Date: 16-03-18
 * |
 * | Project Info:
 * | Project Name: DPI
 * | Project Package Name: mix.messaging.gateway
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class MQChannel {

    private static MQChannel _instance;
    private Channel channel;

    public static MQChannel getInstance() {
        if(_instance == null)  {
            new MQChannel();
        }

        return _instance;
    }

    private MQChannel() {
        _instance = this;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection;

        try {
            connection = factory.newConnection();
            this.channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }

    public Channel getCurrentChannel(String channelName) throws IOException {
        this.channel.queueDeclare(channelName, false, false, false, null);

        return this.channel;
    }
}

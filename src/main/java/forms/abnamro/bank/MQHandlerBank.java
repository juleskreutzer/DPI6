package forms.abnamro.bank;

import com.rabbitmq.client.*;
import mix.ChannelNames;
import mix.messaging.gateway.MQChannel;
import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * | Created by juleskreutzer
 * | Date: 16-03-18
 * |
 * | Project Info:
 * | Project Name: DPI
 * | Project Package Name: forms.abnamro.bank
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class MQHandlerBank {

    private ArrayList<IReceiveInterestRequestMessage> listeners;

    public MQHandlerBank() {
        listeners = new ArrayList<>();
        try {
            Channel channel = MQChannel.getInstance().getCurrentChannel(ChannelNames.BANK_INTEREST_REQUEST);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("Received message from " + consumerTag);
                    for(IReceiveInterestRequestMessage receiver : listeners) {
                        receiver.ReceiveBankInterestRequest(((RequestReply<BankInterestRequest, BankInterestReply>) SerializationUtils.deserialize(body)).getRequest());
                    }
                }
            };

            channel.basicConsume(ChannelNames.BANK_INTEREST_REQUEST, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addListener(IReceiveInterestRequestMessage listener) {
        listeners.add(listener);
    }

    public void sendReply(BankInterestReply message, String channelName) {
        try {
            RequestReply<BankInterestRequest, BankInterestReply> reply = new RequestReply<>(null, message);
            MQChannel.getInstance().getCurrentChannel(channelName).basicPublish("", channelName, null, SerializationUtils.serialize(reply));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package forms.loanclient;

import com.rabbitmq.client.*;
import mix.ChannelNames;
import mix.messaging.gateway.MQChannel;
import mix.messaging.requestreply.RequestReply;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * | Created by juleskreutzer
 * | Date: 18-03-18
 * |
 * | Project Info:
 * | Project Name: DPI
 * | Project Package Name: forms.loanclient
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class MQHandlerLoanClient {

    private ArrayList<IReceiveLoanReplyMessage> listeners;

    public MQHandlerLoanClient() {
        listeners = new ArrayList<>();

        try {
            Channel channel = MQChannel.getInstance().getCurrentChannel(ChannelNames.LOAN_REPLY);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    for(IReceiveLoanReplyMessage receiver : listeners) {
                        receiver.ReceiveLoanReplyMessage(((RequestReply<LoanRequest, LoanReply>) SerializationUtils.deserialize(body)).getReply());
                    }
                }
            };

            channel.basicConsume(ChannelNames.LOAN_REPLY, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListener(IReceiveLoanReplyMessage listener) {
        listeners.add(listener);
    }

}

package forms.loanbroker;

import com.rabbitmq.client.*;
import mix.ChannelNames;
import mix.messaging.gateway.MQChannel;
import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import org.apache.commons.lang.SerializationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;


public class LoanBrokerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
	private JList<JListLine> list;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoanBrokerFrame frame = new LoanBrokerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public LoanBrokerFrame() {
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		list = new JList<JListLine>(listModel);
		scrollPane.setViewportView(list);

		this.setupMQ();
	}
	
	 private JListLine getRequestReply(LoanRequest request){    
	     
	     for (int i = 0; i < listModel.getSize(); i++){
	    	 JListLine rr =listModel.get(i);
	    	 if (rr.getLoanRequest() == request){
	    		 return rr;
	    	 }
	     }
	     
	     return null;
	   }
	
	public void add(LoanRequest loanRequest){
		listModel.addElement(new JListLine(loanRequest));		
	}
	

	public void add(LoanRequest loanRequest,BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}
	
	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);;
            list.repaint();
		}		
	}

	private void setupMQ() {
		try {

			/**
			 * LoanRequest setup
			 */

			Channel loanRequestChannel = MQChannel.getInstance().getCurrentChannel(ChannelNames.LOAN_REQUEST);
			Consumer loanRequestConsumer = new DefaultConsumer(loanRequestChannel) {
				@Override
				public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) {
					System.out.println("Received message from " + s);

					RequestReply<LoanRequest, LoanReply> rr = (RequestReply<LoanRequest, LoanReply>) SerializationUtils.deserialize(bytes);

					add(rr.getRequest());

					RequestReply<BankInterestRequest, BankInterestReply> request = new RequestReply<>(new BankInterestRequest(rr.getRequest().getAmount(), rr.getRequest().getTime()), null);

					add(rr.getRequest(), request.getRequest());
					try {
						sendMessage(request, ChannelNames.BANK_INTEREST_REQUEST);

					} catch (IOException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}

				}
			};

			loanRequestChannel.basicConsume(ChannelNames.LOAN_REQUEST, true, loanRequestConsumer);


			/**
			 * BankInterestReply setu
			 */

			Channel bankInterestReplyChannel = MQChannel.getInstance().getCurrentChannel(ChannelNames.BANK_INTEREST_REPLY);
			Consumer bankInterestReplyConsumer = new DefaultConsumer(bankInterestReplyChannel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					RequestReply<BankInterestRequest, BankInterestReply> rr = (RequestReply<BankInterestRequest, BankInterestReply>) SerializationUtils.deserialize(body);

					RequestReply<LoanRequest, LoanReply> reply = new RequestReply<>(null, new LoanReply(rr.getReply().getInterest(), rr.getReply().getQuoteId()));

					//TODO: Add the reply an new message to the Jlist

					try {
						sendMessage(reply, ChannelNames.LOAN_REPLY);
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			};

			bankInterestReplyChannel.basicConsume(ChannelNames.BANK_INTEREST_REPLY, true, bankInterestReplyConsumer);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void sendMessage(Object message, String channelName) throws IOException, TimeoutException {
		MQChannel.getInstance().getCurrentChannel(channelName).basicPublish("", channelName, null, SerializationUtils.serialize((Serializable) message));
	}


}

package forms.loanclient;

import mix.model.loan.LoanReply;

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
public interface IReceiveLoanReplyMessage {

    void ReceiveLoanReplyMessage(LoanReply message);
}

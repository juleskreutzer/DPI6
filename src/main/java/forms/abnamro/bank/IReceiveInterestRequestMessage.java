package forms.abnamro.bank;

import mix.model.bank.BankInterestRequest;

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
public interface IReceiveInterestRequestMessage {

    void ReceiveBankInterestRequest(BankInterestRequest request);
}

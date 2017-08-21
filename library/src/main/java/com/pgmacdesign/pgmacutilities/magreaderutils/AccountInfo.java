
package com.pgmacdesign.pgmacutilities.magreaderutils;


public class AccountInfo {


    private CardConstants.IndustryIdentifier industryIdentifier;
    private boolean isAccountNumValid;
    private boolean passLuhnCheck;
    private boolean isValidLength;
    private int accountNumLength;
    private CardConstants.CardTypes cardType;
    private boolean isTooLong;

    public AccountInfo(CardConstants.IndustryIdentifier industryIdentifier,
                       CardConstants.CardTypes cardType, boolean passLuhnCheck,
                       int accountNumLength, boolean isValidLength,
                       boolean isAccountNumValid, boolean isTooLong) {
        this.industryIdentifier = industryIdentifier;
        this.isAccountNumValid = isAccountNumValid;
        this.accountNumLength = accountNumLength;
        this.passLuhnCheck = passLuhnCheck;
        this.isValidLength = isValidLength;
        this.cardType = cardType;
        this.isTooLong = isTooLong;
    }

    public CardConstants.IndustryIdentifier getIndustryIdentifier() {
        return industryIdentifier;
    }

    public boolean isAccountNumValid() {
        return isAccountNumValid;
    }

    public boolean isPassLuhnCheck() {
        return passLuhnCheck;
    }

    public boolean isValidLength() {
        return isValidLength;
    }

    public int getAccountNumLength() {
        return accountNumLength;
    }

    public CardConstants.CardTypes getCardType() {
        return cardType;
    }

    public boolean isTooLong() {
        return isTooLong;
    }
}

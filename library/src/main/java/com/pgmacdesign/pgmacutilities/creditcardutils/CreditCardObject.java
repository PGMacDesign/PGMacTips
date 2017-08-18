
package com.pgmacdesign.pgmacutilities.creditcardutils;


import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.util.Date;

/**
 * Represents a card (IE Credit, Debit) and contains information about the card
 */
public class CreditCardObject {

    private final AccountNumber accountNumber;
    private final Name name;
    private final ExpirationDateObject expirationDateObject;
    private final ServiceCode serviceCode;
    private CardConstants.CardMisc cardInfo;
    private String firstName, lastName;

    public void parseRawName(String rawName) {
        String[] fullName = StringUtilities.removeSpaces(rawName).split("/");
        if (fullName.length >= 2) {
            firstName = fullName[1];
            lastName = fullName[0];
        } else {
            //Error parsing
        }
    }



    public CreditCardObject() {
        this(null);
    }

    public CreditCardObject(final AccountNumber accountNumber) {
        this(accountNumber, null);
    }

    public CreditCardObject(AccountNumber accountNumber, ExpirationDateObject expirationDateObject) {
        this(accountNumber, expirationDateObject, null);
    }

    public CreditCardObject(AccountNumber accountNumber,
                            ExpirationDateObject expirationDateObject, Name name) {
        this(accountNumber, expirationDateObject, name, null);
    }


    public CreditCardObject(AccountNumber accountNumber,
                            ExpirationDateObject expirationDateObject, Name name,
                            ServiceCode serviceCode) {
        if (accountNumber != null) {
            this.accountNumber = accountNumber;
        } else {
            this.accountNumber = new AccountNumber(null);
        }

        if (name != null) {
            this.name = name;
        } else {
            this.name = new Name();
        }

        if (expirationDateObject != null) {
            this.expirationDateObject = expirationDateObject;
        } else {
            this.expirationDateObject = new ExpirationDateObject();
        }

        if (serviceCode != null) {
            this.serviceCode = serviceCode;
        } else {
            this.serviceCode = new ServiceCode();
        }
    }

    public String getAccountNumber() {
        return accountNumber.getAccountNumber();
    }
    public String getCardHolderName() {
        return name.getFullName();
    }
    public ExpirationDateObject getExpirationDateObject() {
        return expirationDateObject;
    }
    public Date getExpirationDateAsDate() {
        return expirationDateObject.getExpirationDateAsDate();
    }
    public Name getName() {
        return name;
    }
    public AccountNumber getPrimaryAccountNumber() {
        return accountNumber;
    }
    public ServiceCode getServiceCode() {
        return serviceCode;
    }
    public boolean hasExpirationDate() {
        return expirationDateObject != null && expirationDateObject.hasExpirationDate();
    }
    public boolean hasPrimaryAccountNumber() {
        return accountNumber != null && accountNumber.hasAccountNumber();
    }
    public boolean hasServiceCode() {
        return serviceCode != null && serviceCode.hasServiceCode();
    }
    public boolean isExpired() {
        return expirationDateObject.isExpired();
    }

    public CardConstants.CardMisc getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardConstants.CardMisc cardInfo) {
        this.cardInfo = cardInfo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

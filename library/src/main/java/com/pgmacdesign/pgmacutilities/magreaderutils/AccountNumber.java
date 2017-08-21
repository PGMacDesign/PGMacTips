
package com.pgmacdesign.pgmacutilities.magreaderutils;


import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.util.Arrays;

import static com.pgmacdesign.pgmacutilities.utilities.StringUtilities.isNullOrEmpty;

public class AccountNumber extends BaseTempData implements TempStringInterface {

    private TempString accountNumber;
    private TempString lastFourDigits;
    private AccountInfo accountInfo;

    public AccountNumber() {
        this(null);
    }

    public AccountNumber(String rawAccountNumber) {
        super(rawAccountNumber);
        accountNumber = new TempString(null);
        lastFourDigits = new TempString(null);
        accountInfo = null;
        if(isNullOrEmpty(rawAccountNumber)){
            return;
        }
        String accountNumberString = parseAccountNumber(StringUtilities.removeSpaces(rawAccountNumber));
        accountNumber = new TempString(accountNumberString);
        if(!isNullOrEmpty(accountNumberString)) {
            return;
        }
        if (accountNumberString.length() >= 4) {
            lastFourDigits = new TempString(accountNumberString.substring(
                    (accountNumberString.length() - 4)));
        }

        boolean passesLuhnCheck = luhnCheck();
        CardConstants.IndustryIdentifier majorIndustryIdentifier =
                CardConstants.IndustryIdentifier.parse(accountNumberString);
        CardConstants.CardTypes cardType =
                CardConstants.CardTypes.parseCardType(accountNumberString);
        int accountNumberLength = accountNumberString.length();
        boolean isLengthValid = Arrays.asList(13, 14, 15, 16, 19)
                .contains(accountNumberLength);
        boolean isPrimaryAccountNumberValid = (hasAccountNumber() &&
                isLengthValid && passesLuhnCheck &&
                cardType != CardConstants.CardTypes.Unknown);
        boolean exceedsMaximumLength = accountNumberLength > 19;
        accountInfo = new AccountInfo(majorIndustryIdentifier,
                cardType, passesLuhnCheck, accountNumberLength,
                isLengthValid, isPrimaryAccountNumberValid, exceedsMaximumLength);
    }

    /**
     * @see {@link #dispose}
     */
    @Deprecated
    public void clear() {
        dispose();
    }

    /**
     * @see {@link #disposeIssuerIdentificationNumber}
     */
    @Deprecated
    public void clearIssuerIdentificationNumber() {
        disposeIssuerIdentificationNumber();
    }

    /**
     * @see {@link #disposeLastFourDigits}
     */
    @Deprecated
    public void clearLastFourDigits() {
        disposeLastFourDigits();
    }

    public void dispose() {
        super.clearTempString();
        disposeLastFourDigits();
        disposeIssuerIdentificationNumber();
        accountNumber.disposeData();
    }

    public void disposeIssuerIdentificationNumber() {
        super.clearTempString();
        accountNumber.disposeData(0, 6);
    }

    public void disposeLastFourDigits() {
        super.clearTempString();
        lastFourDigits.disposeData();
        accountNumber.disposeData(accountNumber.length() - 4);
    }


    public String getAccountNumber() {
        return accountNumber.getTempStringData();
    }

    public int getAccountNumberLength() {
        return accountInfo.getAccountNumLength();
    }

    public String getLastFourDigits() {
        return lastFourDigits.getTempStringData();
    }

    public CardConstants.IndustryIdentifier getMajorIndustryIdentifier() {
        return accountInfo.getIndustryIdentifier();
    }

    public boolean hasAccountNumber() {
        return accountNumber.thereIsData();
    }

    public boolean hasLastFourDigits() {
        return lastFourDigits.thereIsData();
    }

    public boolean isLengthValid() {
        return accountInfo.isValidLength();
    }

    public boolean isPrimaryAccountNumberValid() {
        return accountInfo.isAccountNumValid();
    }

    public boolean passesLuhnCheck() {
        return accountInfo.isPassLuhnCheck();
    }

    private boolean luhnCheck() {
        int length = accountNumber.length();
        int sum = 0;
        boolean alternate = false;
        for (int i = (length - 1); i >= 0; i--) {
            int digit = Character.digit(accountNumber.charAt(i), 10);
            if (alternate) {
                digit *= 2;
                digit = (digit > 9) ? (digit - 9) : digit;
            }
            sum += digit;
            //Flip back
            alternate = !alternate;
        }
        return ((sum % 10) == 0);
    }

    private String parseAccountNumber(String rawAccountNumber) {
        if (isNullOrEmpty(rawAccountNumber)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int length = rawAccountNumber.length();
        for (int offset = 0; offset < length; ) {
            int codepoint = rawAccountNumber.codePointAt(offset);
            if (Character.isDigit(codepoint)) {
                int digit = Character.digit(codepoint, 10);
                builder.append(String.valueOf(digit));
            }
            offset += Character.charCount(codepoint);
        }
        return builder.toString();
    }

    @Override
    public boolean tempStringTooLong() {
        return accountInfo.isTooLong();
    }
}

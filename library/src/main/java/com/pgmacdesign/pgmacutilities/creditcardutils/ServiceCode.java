
package com.pgmacdesign.pgmacutilities.creditcardutils;


import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

public class ServiceCode extends BaseTempData {

    private String serviceCode;
    private CardConstants.CardMisc cardMisc;
    private CardConstants.PinRequirements pinRequirements;

    public ServiceCode() {
        this(null);
    }

    public ServiceCode(final String rawServiceCode) {
        super(rawServiceCode);

        serviceCode = CardConstants.NO_NUMBERS.matcher(
                StringUtilities.removeSpaces(rawServiceCode)).replaceAll("");
        cardMisc = serviceCode(0, cardMisc.unknown);
        pinRequirements = serviceCode(2, pinRequirements.unknown);
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public CardConstants.CardMisc getCardMisc() {
        return cardMisc;
    }

    public CardConstants.PinRequirements getPinRequirements() {
        return pinRequirements;
    }


    public boolean hasServiceCode() {
        return !(cardMisc == cardMisc.unknown
                || pinRequirements == pinRequirements.unknown);
    }

    @Override
    public String toString() {
        return serviceCode;
    }

    private <S extends Enum<S> & ServiceCodeInterface> S serviceCode(final int position,
                                                                     final S defaultServiceCode) {
        if (serviceCode.length() > position) {
            final int value = Character.digit(serviceCode.charAt(position), 10);
            final S[] serviceCodes = defaultServiceCode.getDeclaringClass()
                    .getEnumConstants();
            for (final S serviceCode : serviceCodes) {
                if (serviceCode.getCode() == value) {
                    return serviceCode;
                }
            }
        }
        return defaultServiceCode;
    }

    @Override
    public boolean tempStringTooLong() {
        return (StringUtilities.removeSpaces(getTempString()).length() > 3);
    }
}

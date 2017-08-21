package com.pgmacdesign.pgmacutilities.magreaderutils;

/**
 * Created by pmacdowell on 2017-08-02.
 */

public class CardInfo {
    private CardConstants.PinRequirements pinRequirements;
    private Boolean isInternational;
    private Boolean hasChip;

    public CardConstants.PinRequirements getPinRequirements() {
        return pinRequirements;
    }

    public void setPinRequirements(CardConstants.PinRequirements pinRequirements) {
        this.pinRequirements = pinRequirements;
    }

    public Boolean getInternational() {
        return isInternational;
    }

    public void setInternational(Boolean international) {
        isInternational = international;
    }

    public Boolean getHasChip() {
        return hasChip;
    }

    public void setHasChip(Boolean hasChip) {
        this.hasChip = hasChip;
    }
}

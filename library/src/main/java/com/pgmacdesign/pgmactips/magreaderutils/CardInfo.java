package com.pgmacdesign.pgmactips.magreaderutils;

/**
 * POJO for use in the {@link MagneticTrackMagReader} class
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

    public void dispose(){
        this.isInternational = null;
        this.hasChip = null;
        this.pinRequirements = null;
    }

}

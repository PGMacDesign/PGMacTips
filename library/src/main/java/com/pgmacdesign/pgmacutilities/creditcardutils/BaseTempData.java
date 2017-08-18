
package com.pgmacdesign.pgmacutilities.creditcardutils;


/**
 * Class is used to wipe data parse the Strings used as a security measure.
 */
public abstract class BaseTempData implements TempStringInterface {

    private transient final TempString tempString;

    protected BaseTempData(final String tempString) {
        if (tempString != null) {
            this.tempString = new TempString(tempString);
        } else {
            this.tempString = new TempString("");
        }
    }

    @Override
    public boolean tempStringHasData() {
        return this.tempString.thereIsData();
    }

    @Override
    public String getTempString() {
        if (tempString != null) {
            return this.tempString.getTempStringData();
        } else {
            return null;
        }
    }

    @Override
    public void clearTempString() {
        this.tempString.disposeData();
    }

}
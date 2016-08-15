package com.pgmacdesign.pgmacutilities.utilities;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;

import java.util.regex.Pattern;

/**
 * Created by pmacdowell on 8/15/2016.
 */
public class CreditCardUtilities {


    /**
     * Checks credit card type. Enum matches via Regular Expressions
     */
    public static enum CardType {
        UNKNOWN,
        VISA(PGMacUtilitiesConstants.CARD_REGEX_VISA),
        MASTERCARD(PGMacUtilitiesConstants.CARD_REGEX_MASTERCARD),
        AMERICAN_EXPRESS(PGMacUtilitiesConstants.CARD_REGEX_AMERICAN_EXPRESS),
        DINERS_CLUB(PGMacUtilitiesConstants.CARD_REGEX_DINERS_CLUB),
        DISCOVER(PGMacUtilitiesConstants.CARD_REGEX_DISCOVER),
        JCB(PGMacUtilitiesConstants.CARD_REGEX_JCB);

        private Pattern pattern;

        CardType() {
            this.pattern = null;
        }

        CardType(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        /**
         * Detects the cardType.
         * @param cardNumber Credit Card number being checked
         * @return
         */
        public static CardType detect(String cardNumber) {

            for (CardType cardType : CardType.values()) {
                if (null == cardType.pattern) continue;
                if (cardType.pattern.matcher(cardNumber).matches()) return cardType;
            }

            return UNKNOWN;
        }

    }

    /*
    Example of how to use:

    //Check credit card type here
            CreditCardUtilities.CardType cardType = CreditCardUtilities.CardType.UNKNOWN;
            cardType = CreditCardUtilities.CardType.detect(ccInput); //Input of Credit Card String goes here
            switch (cardType){
                case AMERICAN_EXPRESS:
                    add_new_card_card_image.setBackgroundColor(Constants.COLOR_SALMON);
                    break;
                case VISA:
                    add_new_card_card_image.setBackgroundColor(Constants.COLOR_BLACK);
                    break;
                case MASTERCARD:
                    add_new_card_card_image.setBackgroundColor(Constants.COLOR_WONDER_DISABLED);
                    break;
                case DINERS_CLUB:
                    add_new_card_card_image.setBackgroundColor(Constants.COLOR_LINEN);
                    break;
                case DISCOVER:
                    add_new_card_card_image.setBackgroundColor(Constants.COLOR_WONDER_TURQUOISE);
                    break;
                case JCB:
                    add_new_card_card_image.setBackgroundColor(Constants.COLOR_LIGHT_BLUE);
                    break;
                case UNKNOWN:
                    add_new_card_card_image.setBackgroundColor(Constants.COLOR_TRANSPARENT);
                    break;
            }
     */

    /**
     * Formats the text while the user types for Expiration Date
     * @param et The edit text being worked on
     * @param textWatcher Text watcher to both remove and add back on. (Pass 'this' from class)
     * @param <T> T extends EditText
     */
    public static <T extends EditText> void formatExpirationDateWhileTyping(T et, TextWatcher textWatcher){
        if(et == null) {
            return;
        }
        if(textWatcher == null){
            return;
        }
        Editable s = et.getText();
        if(s == null){
            return;
        }

        if (s.length() > 0) {
            String input = s.toString();
            input = input.replace("/", "");
            input = input.replace(" / ",  "");
            if(input.length() <= 2){
            } else if(input.length() >=3 && input.length()<= 7){
                String sub1 = input.substring(0, 2);
                String sub2 = input.substring(2);
                String toSet = sub1 + "/" + sub2;

                et.removeTextChangedListener(textWatcher);
                et.setText(toSet);
                et.setSelection(toSet.length());
                et.addTextChangedListener(textWatcher);
            }
        }
    }

    /**
     * Formats a credit card edit text by adding things like hyphens every 4 numbers
     * @param et The edit text being worked on
     * @param textWatcher Text watcher to both remove and add back on. (Pass 'this' from class)
     * @param <T> T extends EditText
     */
    public static <T extends EditText> void formatCardNumberWhileTyping(T et, TextWatcher textWatcher){
        if(et == null) {
            return;
        }
        if(textWatcher == null){
            return;
        }
        Editable s = et.getText();
        if(s == null){
            return;
        }
        Pattern CODE_PATTERN = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
        if (s.length() > 0 && !CODE_PATTERN.matcher(s).matches()) {
            String input = s.toString();
            String numbersOnly = StringUtilities.keepNumbersOnly(input);
            String code = StringUtilities.formatNumbersAsCreditCard(numbersOnly);

            et.removeTextChangedListener(textWatcher);
            et.setText(code);
            et.setSelection(code.length());
            et.addTextChangedListener(textWatcher);

            if(numbersOnly.length() > 16){
                //L.toast(this, "Too long!");
            }
        }
    }

    /**
     * Checks the card expiration data
     * @param expiryDateString Takes in a String formatted like any of these (Note the leading Zero):
     *                         0219 <-- Feb, 2019
     *                         0412 <-- April, 2012
     *                         122020 <-- December, 2020
     *                         0121 <-- January, 2021
     * @return Boolean, true if data is good, false if it is bad
     */
    public static boolean checkExpirationData(String expiryDateString){
        if(StringUtilities.isNullOrEmpty(expiryDateString)){
            return false;
        }
        String typedData = StringUtilities.keepNumbersOnly(expiryDateString);
        typedData = typedData.trim();
        String expiryMonth2 = null, expiryYear2 = null;

        if(typedData.length() == 5 || typedData.length() > 6){
            return false;
        } else {
            //Sub-string them out
            try {
                expiryMonth2 = typedData.substring(0, 2);
                expiryYear2 = typedData.substring(2);
            } catch (Exception e){
                e.printStackTrace();
            }
            if(expiryYear2 == null || expiryMonth2 == null){
                return false;
            } else {
                if(expiryYear2.isEmpty() || expiryMonth2.isEmpty()){
                    return false;
                } else {
                    //Check lengths individually for final confirmation
                    if(expiryMonth2.length() != 2){
                        return false;
                    } else {
                        //Month is correct, move to Year
                        //If they put a 2 digit year, auto correct to add '20' in front
                        if(expiryYear2.length() < 2){
                            return false;
                        } else {
                            if(expiryYear2.length() == 2){
                                //Year is now correct
                                expiryYear2 = "20" + expiryYear2;
                            }
                            if(expiryYear2.length() != 4){ //Should be 4 now no matter what
                                return false;
                            }
                        }
                    }
                }
            }
        }

        //Month and Year Strings now formatted like this: 12/2018
        //Send the data to be checked for validation
        return checkExpirationData(expiryMonth2, expiryYear2);
    }

    /**
     * Checks the credit card expiration data
     * @param expiryMonth Month in String format (IE 12, 02, 04, 10))
     * @param expiryYear Year in String format (IE 2020, 2050, 2001)
     * @return Boolean, true if data is good, false if it is bad
     */
    public static boolean checkExpirationData(String expiryMonth, String expiryYear){
        if(StringUtilities.isNullOrEmpty(expiryYear)){
            return false;
        }
        if(StringUtilities.isNullOrEmpty(expiryMonth)){
            return false;
        }

        //Update your data here to check against month and year
        int currentMonth = DateUtilities.getCurrentMonth();
        int currentYear = DateUtilities.getCurrentYear();
        int expiryMonthInt = Integer.parseInt(expiryMonth);
        int expiryYearInt = Integer.parseInt(expiryYear);

        boolean badMonth, badYear, badCard;

        //Month
        if(expiryMonthInt <= currentMonth){
            //Month is bad
            badMonth = true;
        } else {
            badMonth = false;
        }

        //Year
        if(expiryYearInt <= currentYear){
            if(expiryYearInt == currentYear){
                //Could be bad, check month
                if(badMonth){
                    //Current Year, current or past month = bad
                    badCard = true;
                } else {
                    //Current year, future month = good
                    badCard = false;
                }
            } else {
                //Less than, def bad
                badYear = true;
                badCard = true;
            }
        } else {
            badYear = false;
            badCard = false;
        }

        if(badCard){
            return false;
        } else {
            return true;
        }
    }
    /**
     * This method checks the validity of a credit card via typeCard
     * @param typeCard Type of card being passed in of type CreditCardUtilities.CardType
     * @param imageToSet ImageView to set the credit card logo image into
     * @param <T> T extends ImageView
     * @return Boolean, true if the card is a valid one (IE Not unknown or default)
     */
    public static <T extends ImageView> boolean checkCreditCardNumber(CreditCardUtilities.CardType typeCard,
                                                                      T imageToSet) {
        Integer imageResourceIdToset;
        boolean valueToReturn;
        switch (typeCard){
            case AMERICAN_EXPRESS:
                imageResourceIdToset = R.drawable.amex;
                valueToReturn = true;
                break;

            case VISA:
                imageResourceIdToset = R.drawable.visa;
                valueToReturn = true;
                break;

            case MASTERCARD:
                imageResourceIdToset = R.drawable.mastercard;
                valueToReturn = true;
                break;

            case DINERS_CLUB:
                imageResourceIdToset = R.drawable.diners;
                valueToReturn = true;
                break;

            case DISCOVER:
                imageResourceIdToset = R.drawable.discover;
                valueToReturn = true;
                break;

            case JCB:
                imageResourceIdToset = R.drawable.jcb;
                valueToReturn = true;
                break;

            case UNKNOWN:
                imageResourceIdToset = null;
                valueToReturn = false;
                break;

            default:
                imageResourceIdToset = null;
                valueToReturn = false;
                break;
        }

        if(imageResourceIdToset == null){
            imageToSet.setImageDrawable(null);
        } else {
            imageToSet.setImageResource(imageResourceIdToset);
        }

        return valueToReturn;
    }
    /**
     * This method checks the validity of a credit card via typeCard
     * @param typeCard Type of card being passed in of type CreditCardUtilities.CardType
     * @param imageToSet ImageView to set the credit card logo image into
     * @param visibilityTypeIfWrongCard The visibility type to set if the wrong card (IE Visibility.GONE)
     * @param visibilityTypeIfRightCard The visibility type to set if the right card (IE Visibility.VISIBLE)
     * @param onlyTypeTrue Object of type CreditCardUtilities.CardType. Will only return true if the
     *                     decided card is of this type. This would be used if you only
     *                     want to allow 1 specific card type (IE Visa only)
     * @param <T> T extends ImageView
     * @return Boolean, true if the card is a valid one and matches passed type
     */
    public static <T extends ImageView> boolean checkCreditCardNumber(CreditCardUtilities.CardType typeCard,
                                                                      T imageToSet,
                                                                      int visibilityTypeIfWrongCard,
                                                                      int visibilityTypeIfRightCard,
                                                                      CreditCardUtilities.CardType onlyTypeTrue){
        Integer imageResourceIdToset;
        boolean valueToReturn;
        switch (typeCard){
            case AMERICAN_EXPRESS:
                imageResourceIdToset = R.drawable.amex;
                if(onlyTypeTrue == CardType.AMERICAN_EXPRESS){
                    valueToReturn = true;
                } else {
                    valueToReturn = false;
                }
                break;

            case VISA:
                imageResourceIdToset = R.drawable.visa;
                if(onlyTypeTrue == CardType.VISA){
                    valueToReturn = true;
                } else {
                    valueToReturn = false;
                }
                break;

            case MASTERCARD:
                imageResourceIdToset = R.drawable.mastercard;
                if(onlyTypeTrue == CardType.MASTERCARD){
                    valueToReturn = true;
                } else {
                    valueToReturn = false;
                }
                break;

            case DINERS_CLUB:
                imageResourceIdToset = R.drawable.diners;
                if(onlyTypeTrue == CardType.DINERS_CLUB){
                    valueToReturn = true;
                } else {
                    valueToReturn = false;
                }
                break;

            case DISCOVER:
                imageResourceIdToset = R.drawable.discover;
                if(onlyTypeTrue == CardType.DISCOVER){
                    valueToReturn = true;
                } else {
                    valueToReturn = false;
                }
                break;

            case JCB:
                imageResourceIdToset = R.drawable.jcb;
                if(onlyTypeTrue == CardType.JCB){
                    valueToReturn = true;
                } else {
                    valueToReturn = false;
                }
                break;

            case UNKNOWN:
                imageResourceIdToset = null;
                valueToReturn = false;
                break;

            default:
                imageResourceIdToset = null;
                valueToReturn = false;
                break;
        }

        if(imageResourceIdToset != null){
            imageToSet.setImageResource(imageResourceIdToset);
        } else {
            imageToSet.setImageDrawable(null);
        }
        if(valueToReturn){
            imageToSet.setVisibility(visibilityTypeIfRightCard);
        } else {
            imageToSet.setVisibility(visibilityTypeIfWrongCard);
        }

        return valueToReturn;
    }

    /**
     * This method checks the validity of a credit card via typeCard
     * @param typeCard Type of card being passed in of type CreditCardUtilities.CardType
     * @param imageToSet ImageView to set the credit card logo image into
     * @param visibilityTypeIfWrongCard The visibility type to set if the wrong card (IE Visibility.GONE)
     * @param visibilityTypeIfRightCard The visibility type to set if the right card (IE Visibility.VISIBLE)
     * @param onlyTypeTrue Array of type CreditCardUtilities.CardType. Will only return true if the
     *                     decided card is of one of these types. This would be used if you only
     *                     want to allow specific card types (IE Visa && AMEX && Mastercard)
     * @param <T> T extends ImageView
     * @return Boolean, true if the card is a valid one and matches passed type
     */
    public static <T extends ImageView> boolean checkCreditCardNumber(CreditCardUtilities.CardType typeCard,
                                                                      T imageToSet,
                                                                      int visibilityTypeIfWrongCard,
                                                                      int visibilityTypeIfRightCard,
                                                                      CreditCardUtilities.CardType[] onlyTypeTrue){
        Integer imageResourceIdToset;
        Boolean valueToReturn = null;
        switch (typeCard){
            case AMERICAN_EXPRESS:
                imageResourceIdToset = R.drawable.amex;
                for(CreditCardUtilities.CardType aType : onlyTypeTrue){
                    if(aType == CardType.AMERICAN_EXPRESS){
                        valueToReturn = true;
                        break;
                    } else {
                        valueToReturn = false;
                    }
                }
                if(valueToReturn == null){
                    valueToReturn = false;
                }
                break;

            case VISA:
                imageResourceIdToset = R.drawable.visa;
                for(CreditCardUtilities.CardType aType : onlyTypeTrue){
                    if(aType == CardType.VISA){
                        valueToReturn = true;
                        break;
                    } else {
                        valueToReturn = false;
                    }
                }
                if(valueToReturn == null){
                    valueToReturn = false;
                }
                break;

            case MASTERCARD:
                imageResourceIdToset = R.drawable.mastercard;
                for(CreditCardUtilities.CardType aType : onlyTypeTrue){
                    if(aType == CardType.MASTERCARD){
                        valueToReturn = true;
                        break;
                    } else {
                        valueToReturn = false;
                    }
                }
                if(valueToReturn == null){
                    valueToReturn = false;
                }
                break;

            case DINERS_CLUB:
                imageResourceIdToset = R.drawable.diners;
                for(CreditCardUtilities.CardType aType : onlyTypeTrue){
                    if(aType == CardType.DINERS_CLUB){
                        valueToReturn = true;
                        break;
                    } else {
                        valueToReturn = false;
                    }
                }
                if(valueToReturn == null){
                    valueToReturn = false;
                }
                break;

            case DISCOVER:
                imageResourceIdToset = R.drawable.discover;
                for(CreditCardUtilities.CardType aType : onlyTypeTrue){
                    if(aType == CardType.DISCOVER){
                        valueToReturn = true;
                        break;
                    } else {
                        valueToReturn = false;
                    }
                }
                if(valueToReturn == null){
                    valueToReturn = false;
                }
                break;

            case JCB:
                imageResourceIdToset = R.drawable.jcb;
                for(CreditCardUtilities.CardType aType : onlyTypeTrue){
                    if(aType == CardType.JCB){
                        valueToReturn = true;
                        break;
                    } else {
                        valueToReturn = false;
                    }
                }
                if(valueToReturn == null){
                    valueToReturn = false;
                }
                break;

            case UNKNOWN:
                imageResourceIdToset = null;
                valueToReturn = false;
                break;

            default:
                imageResourceIdToset = null;
                valueToReturn = false;
                break;
        }

        if(imageToSet != null) {
            imageToSet.setImageResource(imageResourceIdToset);
            if (valueToReturn) {
                imageToSet.setVisibility(visibilityTypeIfRightCard);
            } else {
                imageToSet.setVisibility(visibilityTypeIfWrongCard);
            }
        } else {
            imageToSet.setImageDrawable(null);
            if (valueToReturn) {
                imageToSet.setVisibility(visibilityTypeIfRightCard);
            } else {
                imageToSet.setVisibility(visibilityTypeIfWrongCard);
            }
        }

        return valueToReturn;
    }

}

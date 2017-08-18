package com.pgmacdesign.pgmacutilities.creditcardutils;

import java.util.regex.Pattern;

import static com.pgmacdesign.pgmacutilities.utilities.StringUtilities.isNullOrEmpty;

/**
 * Created by pmacdowell on 2017-08-02.
 */

public class CardConstants {


    //Regex
    public static final String NO_NUMBERS_REGEX = "[^0-9]";
    public static final String TRACK_1_REGEX = "(%?([A-Z])([0-9]{1,19})\\^([^\\^]{2,26})\\^([0-9]{4}|\\^)([0-9]{3}|\\^)?([^\\?]+)?\\??)[\t\n\r ]{0,2}.*";
    public static final String TRACK_2_REGEX = ".*[\\t\\n\\r ]?(;([0-9]{1,19})=([0-9]{4})([0-9]{3})(.*)\\?).*";
    public static final String TRACK_3_REGEX = ".*?[\t\n\r ]{0,2}(\\+(.*)\\?)";
    public static final String CREDIT_CARD_VISA = "^4[0-9]{6,}$";
    public static final String CREDIT_CARD_MASTERCARD = "^5[1-5][0-9]{5,}$|^(222[1-9]|2[3-6][0-9][0-9]|27[0-1][0-9]|2720)[0-9]{12}$";
    public static final String CREDIT_CARD_AMEX = "^3[47][0-9]{5,}$";
    public static final String CREDIT_CARD_DINERS = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$";
    public static final String CREDIT_CARD_DISCOVER = "^6(?:011|5[0-9]{2})[0-9]{3,}$";
    public static final String CREDIT_CARD_JCB = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$";
    public static final String CREDIT_CARD_UNKNOWN = "^unknown$";


    //Patterns
    public static Pattern NO_NUMBERS = Pattern.compile(NO_NUMBERS_REGEX);
    public static final Pattern TRACK_1_PATTERN = Pattern.compile(CardConstants.TRACK_1_REGEX);
    public static final Pattern TRACK_2_PATTERN = Pattern.compile(CardConstants.TRACK_2_REGEX);
    public static final Pattern TRACK_3_PATTERN = Pattern.compile(CardConstants.TRACK_3_REGEX);
    public static final Pattern PATTERN_CREDIT_CARD_VISA = Pattern.compile(CREDIT_CARD_VISA);
    public static final Pattern PATTERN_CREDIT_CARD_MASTERCARD = Pattern.compile(CREDIT_CARD_MASTERCARD);
    public static final Pattern PATTERN_CREDIT_CARD_AMEX = Pattern.compile(CREDIT_CARD_AMEX);
    public static final Pattern PATTERN_CREDIT_CARD_DINERS = Pattern.compile(CREDIT_CARD_DINERS);
    public static final Pattern PATTERN_CREDIT_CARD_DISCOVER = Pattern.compile(CREDIT_CARD_DISCOVER);
    public static final Pattern PATTERN_CREDIT_CARD_JCB = Pattern.compile(CREDIT_CARD_JCB);
    public static final Pattern PATTERN_CREDIT_CARD_UNKNOWN = Pattern.compile(CREDIT_CARD_UNKNOWN);

    /**
     * From https://en.wikipedia.org/wiki/ISO/IEC_7816
     */
    public static enum CardMisc implements ServiceCodeInterface {
        unknown(-1, "Unknown", ""),
        Misc1(1, "International interchange", "None"),
        Misc2(2, "International interchange", "Integrated circuit card"),
        Misc5(5, "National interchange", "None"),
        Misc6(6, "National interchange", "Integrated circuit card"),
        Misc7(7, "Private", "None"),
        Misc9(9, "Test", "Test"),;

        private final int code;
        private final String internationalAvailability;
        private final String pinRequired;

        CardMisc(int code, String internationalAvailability, String pinRequired) {
            this.code = code;
            this.internationalAvailability = internationalAvailability;
            this.pinRequired = pinRequired;
        }

        public String getInternationalAvailability() {
            return internationalAvailability;
        }

        public String getPinRequired() {
            return pinRequired;
        }

        @Override
        public String getText() {
            return this.internationalAvailability;
        }

        @Override
        public int getCode() {
            return this.code;
        }
    }

    /**
     * Pin Requirements
     * From http://hack2wwworld.blogspot.com/2013/09/carding-tutorial-4-beginners_28.html
     */
    public static enum PinRequirements  implements ServiceCodeInterface {
        unknown(-1, "Unknown", ""),
        PIN0(0, "No restrictions", "PIN required"),
        PIN1(1, "No restrictions", "None"),
        PIN2(2, "Goods and services only", "None"),
        PIN3(3, "ATM only", "PIN required"),
        PIN4(4, "Cash only", "None"),
        PIN5(5, "Goods and services only", "PIN required"),
        PIN6(6, "No restrictions", "Prompt for PIN if PED present"),
        PIN7(7, "Goods and services only", "Prompt for PIN if PED present"),
        PIN8(8, "Reserved for future use by ISO", "N/A"),
        PIN9(9, "Reserved for future use by ISO", "N/A"),;

        private final int code;
        private final String canBeUsedFor;
        private final String pinReqsText;

        PinRequirements(int code, String canBeUsedFor, String pinReqsText) {
            this.code = code;
            this.canBeUsedFor = canBeUsedFor;
            this.pinReqsText = pinReqsText;
        }


        @Override
        public String getText() {
            return this.canBeUsedFor;
        }

        @Override
        public int getCode() {
            return this.code;
        }

        public String getCanBeUsedFor() {
            return canBeUsedFor;
        }

        public String getPinReqsText() {
            return pinReqsText;
        }
    }

    /**
     * Industry identifier. From https://en.wikipedia.org/wiki/ISO/IEC_7812
     */
    public static enum IndustryIdentifier  implements ServiceCodeInterface {
        IndustryIdentUnknown(-1, "unknown"),
        IndustryIdent0(0, "ISO/TC 68 and other future industry assignments"),
        IndustryIdent1(1, "Airlines"),
        IndustryIdent2(2, "Airlines and other future industry assignments"),
        IndustryIdent3(3, "Travel and entertainment and banking/financial"),
        IndustryIdent4(4, "Banking and financial"),
        IndustryIdent5(5, "Banking and financial"),
        IndustryIdent6(6, "Merchandising and banking/financial"),
        IndustryIdent7(7, "Petroleum and other future industry assignments"),
        IndustryIdent8(8, "Healthcare, telecommunications and other future industry assignments"),
        IndustryIdent9(9, "National assignment"),;


        private final int code;
        private final String industry;

        IndustryIdentifier(int code, String industry) {
            this.code = code;
            this.industry = industry;
        }


        @Override
        public String getText() {
            return this.industry;
        }

        @Override
        public int getCode() {
            return this.code;
        }

        public String getIndustry() {
            return industry;
        }

        public static IndustryIdentifier parse(String str){
            if(isNullOrEmpty(str)){
                return IndustryIdentUnknown;
            } else {
                int value = Character.digit(str.charAt(0), 10);
                for(IndustryIdentifier ii : values()){
                    if(value == ii.getCode()){
                        return ii;
                    }
                }
            }
            return IndustryIdentUnknown;
        }
    }

    /**
     * Credit card types
     */
    public static enum CardTypes {
        Unknown(-1, CardConstants.PATTERN_CREDIT_CARD_UNKNOWN),
        Visa(0, CardConstants.PATTERN_CREDIT_CARD_VISA),
        Mastercard(1, CardConstants.PATTERN_CREDIT_CARD_MASTERCARD),
        Amex(2, CardConstants.PATTERN_CREDIT_CARD_AMEX),
        Diners(3, CardConstants.PATTERN_CREDIT_CARD_DINERS),
        Discover(4, CardConstants.PATTERN_CREDIT_CARD_DISCOVER),
        JCB(5, CardConstants.PATTERN_CREDIT_CARD_JCB);

        private final int code;
        private final Pattern pattern;

        CardTypes(int code, Pattern pattern) {
            this.code = code;
            this.pattern = pattern;
        }

        public int getCode() {
            return code;
        }

        /**
         * Simple checker to parse
         * @param cardNumber
         * @return
         */
        public static CardTypes parseCardType(String cardNumber){
            if(isNullOrEmpty(cardNumber)){
                return Unknown;
            }
            for(CardTypes type: values()){
                boolean bool = type.pattern.matcher(cardNumber).matches();
                if(bool){
                    return type;
                }
            }
            return Unknown;
        }


    }


}

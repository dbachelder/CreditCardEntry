package com.devmarvel.creditcardentry.internal;

import android.annotation.SuppressLint;
import android.util.Log;

import com.devmarvel.creditcardentry.library.CardType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO - diners club not yet implemented
 */
@SuppressLint("SimpleDateFormat")
public class CreditCardUtil {
	public static final int CC_LEN_FOR_TYPE = 4; // number of characters to determine length
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy");
	static {
		simpleDateFormat.setLenient(false);
	}

	private static String cleanNumber(String number) {
		return number.replaceAll("\\s", "");
	}

	public static CardType findCardType(String number) {

		if (number.length() < CC_LEN_FOR_TYPE) {
			return CardType.INVALID;
		}

		for (CardType type : CardType.values()) {
			if (type.typeRegex != null) {
				Pattern pattern = Pattern.compile(type.typeRegex);
				Matcher matcher = pattern.matcher(number.substring(0,
            CC_LEN_FOR_TYPE));

				if (matcher.matches()) {
          return type;
        }
			}
		}

		return CardType.INVALID;
	}

	public static boolean isValidNumber(String number) {
		String cleaned = cleanNumber(number);
		CardType cardType = findCardType(cleaned);
		if(cardType == null || cardType.fullRegex == null) return false;

		Pattern pattern = Pattern.compile(cardType.fullRegex);
		Matcher matcher = pattern.matcher(cleaned);

		return matcher.matches() && validateCardNumber(cleaned);
	}

	private static boolean validateCardNumber(String cardNumber)
			throws NumberFormatException {
		int sum = 0, digit, addend;
		boolean doubled = false;
		for (int i = cardNumber.length() - 1; i >= 0; i--) {
			digit = Integer.parseInt(cardNumber.substring(i, i + 1));
			if (doubled) {
				addend = digit * 2;
				if (addend > 9) {
					addend -= 9;
				}
			} else {
				addend = digit;
			}
			sum += addend;
			doubled = !doubled;
		}
		return (sum % 10) == 0;
	}

	public static String formatForViewing(String enteredNumber, CardType type) {
		String cleaned = cleanNumber(enteredNumber);
		int len = cleaned.length();

		if (len <= CC_LEN_FOR_TYPE)
			return cleaned;

		ArrayList<String> gaps = new ArrayList<>();

		int segmentLengths[] = { 0, 0, 0, 0, 0 };

		switch (type) {
		case VISA:
		case MASTERCARD:
		case JCB:
		case DISCOVER: // { 4-4-4-4}
			gaps.add(" ");
			segmentLengths[0] = 4;
			gaps.add(" ");
			segmentLengths[1] = 4;
			gaps.add(" ");
			segmentLengths[2] = 4;
			gaps.add(" ");
			break;
		case AMEX: // {4-6-5}
			gaps.add(" ");
			segmentLengths[0] = 6;
			gaps.add(" ");
			segmentLengths[1] = 5;
			gaps.add("");
			segmentLengths[2] = 0;
			gaps.add(" ");
			break;
		case DINERS:// {4-6-4}
			gaps.add(" ");
			segmentLengths[0] = 6;
			gaps.add(" ");
			segmentLengths[1] = 4;
			gaps.add("");
			segmentLengths[2] = 0;
			gaps.add(" ");
			break;
		case VERVE: // { 4-4-4-4-3}
			gaps.add(" ");
			segmentLengths[0] = 4;
			gaps.add(" ");
			segmentLengths[1] = 4;
			gaps.add(" ");
			segmentLengths[2] = 4;
			gaps.add(" ");
			segmentLengths[3] = 4;
			break;
		default:
			return enteredNumber;
		}

		int end = CC_LEN_FOR_TYPE;
		int start;
		String segment1 = cleaned.substring(0, end);
		start = end;
		end = segmentLengths[0] + end > len ? len : segmentLengths[0] + end;
		String segment2 = cleaned.substring(start, end);
		start = end;
		end = segmentLengths[1] + end > len ? len : segmentLengths[1] + end;
		String segment3 = cleaned.substring(start, end);
		start = end;
		end = segmentLengths[2] + end > len ? len : segmentLengths[2] + end;
		String segment4 = cleaned.substring(start, end);

		start = end;
		end = segmentLengths[3] + end > len ? len : segmentLengths[3] + end;
		String segment5 = cleaned.substring(start, end);

		String ret = String.format("%s%s%s%s%s%s%s%s%s", segment1, gaps.get(0),
				segment2, gaps.get(1), segment3, gaps.get(2), segment4,
				gaps.get(3), segment5);

		return ret.trim();
	}

	public static int lengthOfFormattedStringForType(CardType type) {
		int idx;

		switch (type) {
		case VERVE: //{ 4-4-4-4-3}
			idx = 19 + 4;
			break;
		case VISA:
		case MASTERCARD:
		case JCB:
		case DISCOVER: // { 4-4-4-4}
			idx = 16 + 3;
			break;
		case AMEX: // {4-6-5}
			idx = 15 + 2;
			break;
		case DINERS:// {4-6-4}
			idx = 14 + 2;
			break;
		default:
			idx = 0;
		}
		return idx;
	}

	public static String formatExpirationDate(String text) {
		try {
			switch (text.length()) {
			case 1:
				int digit = Integer.parseInt(text);

				if (digit < 2) {
					return text;
				} else {
					return "0" + text + "/";
				}
			case 2:
				int month = Integer.parseInt(text);
				if (month > 12 || month < 1) {
					// Invalid digit
					return text.substring(0, 1);
				} else {
					return text + "/";
				}
			case 3:
				if (text.substring(2, 3).equalsIgnoreCase("/")) {
					return text;
				} else {
					text = text.substring(0, 2) + "/" + text.substring(2, 3);
				}
			case 4:
                Calendar now = getCurrentExpDate();
                String currentYearStr = String.valueOf(now.get(Calendar.YEAR));

				int yearDigit = Integer.parseInt(text.substring(3, 4));
				int currentYearDigit = Integer.parseInt(currentYearStr.substring(2, 3));
				if (yearDigit < currentYearDigit) {
					// Less than current year invalid
					return text.substring(0, 3);
				} else {
					return text;
				}
			case 5:
                // always make the year in the current century
                Calendar now2 = getCurrentExpDate();
                String currentYearStr2 = String.valueOf(now2.get(Calendar.YEAR));
                String yearStr = text.substring(0,3) + currentYearStr2.substring(0,2) + text.substring(3, 5);
                Date expiry = simpleDateFormat.parse(yearStr);
				if (expiry.before(now2.getTime())) {
					// Invalid exp date
					return text.substring(0, 4);
				} else {
					return text;
				}
			default:
				if (text.length() > 5) {
					return text.substring(0, 5);
				} else {
					return text;
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// If an exception is thrown we clear out the text
		return "";
	}

    private static Calendar getCurrentExpDate() {
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), 0, 0, 0);
        return now;
    }

    public static int securityCodeValid(CardType type) {
		if(type == null) return 3;
		switch (type) {
		case AMEX:
			return 4;
		case DISCOVER:
		case INVALID:
		case MASTERCARD:
		case VISA:
		case DINERS:
		case JCB:
		case VERVE:
		default:
			return 3;
		}
	}
}

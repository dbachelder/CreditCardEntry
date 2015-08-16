package com.devmarvel.creditcardentry.internal;

import android.annotation.SuppressLint;

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

		int segmentLengths[] = { 0, 0, 0 };

		switch (type) {
		case VISA:
		case MASTERCARD:
		case DISCOVER: // { 4-4-4-4}
			gaps.add(" ");
			segmentLengths[0] = 4;
			gaps.add(" ");
			segmentLengths[1] = 4;
			gaps.add(" ");
			segmentLengths[2] = 4;
			break;
		case AMEX: // {4-6-5}
			gaps.add(" ");
			segmentLengths[0] = 6;
			gaps.add(" ");
			segmentLengths[1] = 5;
			gaps.add("");
			segmentLengths[2] = 0;
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

		String ret = String.format("%s%s%s%s%s%s%s", segment1, gaps.get(0),
				segment2, gaps.get(1), segment3, gaps.get(2), segment4);

		return ret.trim();
	}

	public static int lengthOfFormattedStringForType(CardType type) {
		int idx;

		switch (type) {
		case VISA:
		case MASTERCARD:
		case DISCOVER: // { 4-4-4-4}
			idx = 16 + 3;
			break;
		case AMEX: // {4-6-5}
			idx = 15 + 2;
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
				int yearDigit = Integer.parseInt(text.substring(3, 4));
				String year = String.valueOf(Calendar.getInstance().get(
						Calendar.YEAR));
				int currentYearDigit = Integer.parseInt(year.substring(2, 3));
				if (yearDigit < currentYearDigit) {
					// Less than current year invalid
					return text.substring(0, 3);
				} else {
					return text;
				}
			case 5:
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"MM/yy");
				simpleDateFormat.setLenient(false);
				Date expiry = simpleDateFormat.parse(text);
				if (expiry.before(new Date())) {
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

	public static int securityCodeValid(CardType type) {
		if(type == null) return 3;
		switch (type) {
		case AMEX:
			return 4;
		case DISCOVER:
		case INVALID:
		case MASTERCARD:
		case VISA:
		default:
			return 3;
		}
	}
}

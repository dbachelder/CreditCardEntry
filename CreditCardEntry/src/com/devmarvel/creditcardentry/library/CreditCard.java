package com.devmarvel.creditcardentry.library;

public class CreditCard {
	
	private final String cardNumber;
	private final String expDate;
	private final String securityCode;
	private final String zipCode;

	public CreditCard(String cardNumber, String expDate, String securityCode, String zipCode) {
		this.cardNumber = cardNumber;
		this.expDate = expDate;
		this.securityCode = securityCode;
		this.zipCode = zipCode;
	}

	@SuppressWarnings("unused")
	public String getCardNumber() {
		return cardNumber;
	}

	@SuppressWarnings("unused")
	public String getExpDate() {
		return expDate;
	}

	@SuppressWarnings("unused")
	public String getSecurityCode() {
		return securityCode;
	}

	@SuppressWarnings("unused")
	public String getZipCode() {
		return zipCode;
	}

	@SuppressWarnings("unused")
	public Integer getExpMonth() {
		return getDateFragment(0);
	}

	@SuppressWarnings("unused")
	public Integer getExpYear() {
		return getDateFragment(1);
	}

	private Integer getDateFragment(int position) {
		if(expDate != null && expDate.contains("/")) {
			String[] split = expDate.split("/");
			if(split.length > 1) {
				try {
					return Integer.valueOf(split[position]);
				} catch (NumberFormatException ignore) {}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		@SuppressWarnings("StringBufferReplaceableByString") final StringBuilder sb = new StringBuilder("CreditCard{");
		sb.append("cardNumber='").append(cardNumber).append('\'');
		sb.append(", expDate='").append(expDate).append('\'');
		sb.append(", securityCode='").append(securityCode).append('\'');
		sb.append(", zipCode='").append(zipCode).append('\'');
		sb.append('}');
		return sb.toString();
	}
}

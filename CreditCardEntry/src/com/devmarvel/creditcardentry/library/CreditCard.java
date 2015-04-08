package com.devmarvel.creditcardentry.library;

public class CreditCard {
	
	private String cardNumber;
	private String expDate;
	private String securityCode;
	private String zipCode;

	public CreditCard(String cardNumber, String expDate, String securityCode, String zipCode)
	{
		this.setCardNumber(cardNumber);
		this.setExpDate(expDate);
		this.setSecurityCode(securityCode);
		this.setZipCode(zipCode);
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Integer getExpMonth() {
		return getDateFragment(0);
	}

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
		final StringBuilder sb = new StringBuilder("CreditCard{");
		sb.append("cardNumber='").append(cardNumber).append('\'');
		sb.append(", expDate='").append(expDate).append('\'');
		sb.append(", securityCode='").append(securityCode).append('\'');
		sb.append(", zipCode='").append(zipCode).append('\'');
		sb.append('}');
		return sb.toString();
	}
}

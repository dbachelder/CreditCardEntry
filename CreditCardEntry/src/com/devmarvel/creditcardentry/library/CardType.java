package com.devmarvel.creditcardentry.library;

/**
 * represents the type of card the user used
 */
public enum CardType {
  VISA("VISA"), MASTERCARD("MasterCard"), AMEX("American Express"), DISCOVER("Discover"), INVALID("Unknown");

  public final String name;

  CardType(String name) {
    this.name = name;
  }

	@Override
	public String toString() {
		return name;
	}
}

package com.devmarvel.creditcardentry.fields;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;

import com.devmarvel.creditcardentry.R;
import com.devmarvel.creditcardentry.internal.CreditCardUtil;
import com.devmarvel.creditcardentry.internal.CreditCardUtil.CardType;
import com.devmarvel.creditcardentry.internal.CreditCardUtil.CreditCardFieldDelegate;

public class CreditCardText extends CreditEntryFieldBase {

	private CardType type;
	private CreditCardFieldDelegate delegate;

	private String previousNumber;

	public CreditCardText(Context context) {
		super(context);
		init();
	}

	public CreditCardText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CreditCardText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		super.init();
		setGravity(Gravity.LEFT);
		setHint("1234 5678 9012 3456");
	}

	/* TextWatcher Implementation Methods */
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		previousNumber = s.toString();
	}

	public void afterTextChanged(Editable s) {

		String number = s.toString();

		if (number.length() >= CreditCardUtil.CC_LEN_FOR_TYPE) {
			CardType type = CreditCardUtil.findCardType(number);

			if (type.equals(CardType.INVALID)) {
				this.removeTextChangedListener(this);
				this.setText(previousNumber);
				this.setSelection(3);
				this.addTextChangedListener(this);
				delegate.onBadInput(this);
				return;
			}

			if (this.type != type) {
				delegate.onCardTypeChange(type);
			}
			this.type = type;

			String formatted = CreditCardUtil.formatForViewing(number, type);

			if (!number.equalsIgnoreCase(formatted)) {
				Log.i("CreditCardText", formatted);
				this.removeTextChangedListener(this);
				this.setText(formatted);
				this.setSelection(formatted.length());
				this.addTextChangedListener(this);
			}

			if (formatted.length() >= CreditCardUtil
					.lengthOfFormattedStringForType(type)) {
				if (CreditCardUtil.isValidNumber(formatted)) {
					delegate.onCreditCardNumberValid();
				} else {
					delegate.onBadInput(this);
				}
			}

		} else {
			if (this.type != null) {
				this.type = null;
				delegate.onCardTypeChange(CardType.INVALID);
			}
		}
	}

	public CreditCardFieldDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(CreditCardFieldDelegate delegate) {
		this.delegate = delegate;
		delegate.focusOnField(this);
	}

	public CardType getType() {
		return type;
	}

	@Override
	public String helperText() {
		return context.getString(R.string.CreditCardNumberHelp);
	}
}

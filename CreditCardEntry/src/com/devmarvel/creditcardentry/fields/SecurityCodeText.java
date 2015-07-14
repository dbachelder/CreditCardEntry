package com.devmarvel.creditcardentry.fields;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;

import com.devmarvel.creditcardentry.R;
import com.devmarvel.creditcardentry.internal.CreditCardUtil;
import com.devmarvel.creditcardentry.library.CardType;

public class SecurityCodeText extends CreditEntryFieldBase {

	private CardType type;
	
	private int length;

	public SecurityCodeText(Context context) {
		super(context);
		init();
	}

	public SecurityCodeText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SecurityCodeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		super.init();
		setHint("CVV");
	}

	/* TextWatcher Implementation Methods */
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	public void afterTextChanged(Editable s) {
        if(type == null) {
            this.removeTextChangedListener(this);
            this.setText("");
            this.addTextChangedListener(this);
        }
    }

    public void textChanged(CharSequence s, int start, int before, int count) {
		if (type != null) {
			String number = s.toString();

			if (number.length() == length) {
				setValid(true);
				delegate.onSecurityCodeValid();
			} else {
				setValid(false);
			}
		}
	}

	@SuppressWarnings("unused")
	public CardType getType() {
		return type;
	}

	public void setType(CardType type) {
		this.type = type;
		this.length = CreditCardUtil.securityCodeValid(type);
		
		setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
	}

	@Override
	public String helperText() {
		return context.getString(R.string.SecurityCodeHelp);
	}
}

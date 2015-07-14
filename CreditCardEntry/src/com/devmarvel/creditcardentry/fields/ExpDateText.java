package com.devmarvel.creditcardentry.fields;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import com.devmarvel.creditcardentry.R;
import com.devmarvel.creditcardentry.internal.CreditCardUtil;

public class ExpDateText extends CreditEntryFieldBase {

	private String previousString;

	public ExpDateText(Context context) {
		super(context);
		init();
	}

	public ExpDateText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ExpDateText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		super.init();
		setHint("MM/YY");
	}

	/* TextWatcher Implementation Methods */
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		previousString = s.toString();
	}

	public void afterTextChanged(Editable s) {
		String updatedString = s.toString();

		// if delete occurred do not format
		if (updatedString.length() > previousString.length()) {
			this.removeTextChangedListener(this);
			String formatted = CreditCardUtil.formatExpirationDate(s.toString());
			this.setText(formatted);
			this.setSelection(formatted.length());
			this.addTextChangedListener(this);
			
			if(formatted.length() == 5) {
				setValid(true);
				delegate.onExpirationDateValid();
			} else if(formatted.length() < updatedString.length()) {
				setValid(false);
				delegate.onBadInput(this);
			}
		}
	}

	@Override
	public String helperText() {
		return context.getString(R.string.ExpirationDateHelp);
	}
}

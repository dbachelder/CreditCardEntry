package com.devmarvel.creditcardentry.fields;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;

import com.devmarvel.creditcardentry.R;

public class ZipCodeText extends CreditEntryFieldBase {

	public ZipCodeText(Context context) {
		super(context);
		init();
	}

	public ZipCodeText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ZipCodeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		super.init();
		setHint("   ZIP   ");
		setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
	}

	/* TextWatcher Implementation Methods */
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	public void afterTextChanged(Editable s) {
		String zipCode = s.toString();
		if (zipCode.length() == 5) {
			delegate.onZipCodeValid();
			setValid(true);
		}
		else
		{
			setValid(false);
		}
	}

	@Override
	public String helperText() {
		return context.getString(R.string.ZipHelp);
	}
}

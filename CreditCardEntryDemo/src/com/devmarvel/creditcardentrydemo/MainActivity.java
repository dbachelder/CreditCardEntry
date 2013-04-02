package com.devmarvel.creditcardentrydemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.devmarvel.creditcardentry.library.CreditCardForm;

public class MainActivity extends Activity {

	private LinearLayout linearLayout;
	private CreditCardForm form;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		form = new CreditCardForm(this);
		setContentView(R.layout.activity_main);
		linearLayout = (LinearLayout) findViewById(R.id.layer);
		linearLayout.addView(form);
	}

}

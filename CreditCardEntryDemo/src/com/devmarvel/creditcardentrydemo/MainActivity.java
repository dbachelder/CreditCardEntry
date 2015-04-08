package com.devmarvel.creditcardentrydemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;

public class MainActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final CreditCardForm noZipForm = (CreditCardForm) findViewById(R.id.form_no_zip);
		final Button noZipButton = (Button) findViewById(R.id.button_no_zip);

		noZipForm.setOnCardValidCallback(new CardValidCallback() {
			@Override
			public void cardValid(CreditCard card) {
				Toast.makeText(MainActivity.this, "Card valid and complete", Toast.LENGTH_SHORT).show();
			}
		});

		noZipButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (noZipForm.isCreditCardValid()) {
					CreditCard card = noZipForm.getCreditCard();
					//Pass credit card to service
				} else {
					//Alert Credit card invalid
				}
			}
		});

		final CreditCardForm zipForm = (CreditCardForm) findViewById(R.id.form_with_zip);
		final Button zipButton = (Button) findViewById(R.id.button_with_zip);

		zipForm.setOnCardValidCallback(new CardValidCallback() {
			@Override
			public void cardValid(CreditCard card) {
				Toast.makeText(MainActivity.this, "Card valid and complete", Toast.LENGTH_SHORT).show();
			}
		});

		zipButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (zipForm.isCreditCardValid()) {
					CreditCard card = zipForm.getCreditCard();
					//Pass credit card to service
				} else {
					//Alert Credit card invalid
				}
			}
		});
	}

}

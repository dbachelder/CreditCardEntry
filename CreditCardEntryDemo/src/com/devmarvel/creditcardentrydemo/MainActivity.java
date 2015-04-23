package com.devmarvel.creditcardentrydemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final CreditCardForm noZipForm = (CreditCardForm) findViewById(R.id.form_no_zip);

		// we can track gaining or losing focus for any particular field.
		noZipForm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					Toast.makeText(MainActivity.this, v.getClass().getSimpleName() + " gained focus.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		noZipForm.setOnCardValidCallback(new CardValidCallback() {
			@Override
			public void cardValid(CreditCard card) {
				Log.d(TAG, "valid card: " + card);
				Toast.makeText(MainActivity.this, "Card valid and complete", Toast.LENGTH_SHORT).show();
			}
		});

		final CreditCardForm zipForm = (CreditCardForm) findViewById(R.id.form_with_zip);

		zipForm.setOnCardValidCallback(new CardValidCallback() {
			@Override
			public void cardValid(CreditCard card) {
				Log.d(TAG, "valid card: " + card);
				Toast.makeText(MainActivity.this, "Card valid and complete", Toast.LENGTH_SHORT).show();
			}
		});
	}

}

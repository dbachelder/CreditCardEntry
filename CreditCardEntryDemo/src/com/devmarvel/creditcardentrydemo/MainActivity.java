package com.devmarvel.creditcardentrydemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;

public class MainActivity extends Activity {

	private LinearLayout linearLayout;
	private CreditCardForm form;

	private Button buttonAuthorize;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.activity_main);
		linearLayout = (LinearLayout) findViewById(R.id.layer);
		
		form = new CreditCardForm(this);
		linearLayout.addView(form);
		
		buttonAuthorize = (Button) findViewById(R.id.buttonAuthorize);
		buttonAuthorize.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(form.isCreditCardValid())
				{
					CreditCard card = form.getCreditCard();
					//Pass credit card to service
				}
				else
				{
					//Alert Credit card invalid
				}
			}
		});
	}

}

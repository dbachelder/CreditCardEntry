package com.devmarvel.creditcardentry.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devmarvel.creditcardentry.R;
import com.devmarvel.creditcardentry.internal.CreditCardEntry;

public class CreditCardForm extends RelativeLayout {
	
	private CreditCardEntry entry;

	public CreditCardForm(Context context) {
		super(context);
		init(context);
	}

	public CreditCardForm(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CreditCardForm(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context) {

		LinearLayout layout = new LinearLayout(context);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(LinearLayout.HORIZONTAL);
		params.setMargins(10, 40, 10, 0);
		layout.setLayoutParams(params);
		layout.setBackgroundResource(R.drawable.background_grey);

		FrameLayout frame = new FrameLayout(context);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		frame.setLayoutParams(params);
		frame.setPadding(10, 10, 0, 0);

		ImageView view = new ImageView(context);
		LayoutParams r = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(r);
		view.setImageResource(R.drawable.unknown_cc);

		frame.addView(view);

		ImageView backView = new ImageView(context);
		r = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		backView.setLayoutParams(r);
		backView.setImageResource(R.drawable.cc_back);
		backView.setVisibility(View.GONE);

		frame.addView(backView);
		layout.addView(frame);

		layout.setId(1);

		TextView textHelp = new TextView(context);
		textHelp.setText("Enter credit or debit card number");
        textHelp.setTextColor(getResources().getColor(R.color.text_helper_color));
		r = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		r.addRule(RelativeLayout.BELOW, layout.getId());
		r.addRule(RelativeLayout.CENTER_HORIZONTAL);
		r.setMargins(0, 30, 0, 0);
		textHelp.setLayoutParams(r);

		entry = new CreditCardEntry(context);
		r = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		entry.setLayoutParams(r);
		entry.setCardImageView(view);
		entry.setBackCardImage(backView);
		entry.setTextHelper(textHelp);

		layout.addView(entry);
		this.addView(layout);
		this.addView(textHelp);
	}
	
	public boolean isCreditCardValid()
	{
		return entry.isCreditCardValid();
	}
	
	public CreditCard getCreditCard()
	{
		return entry.getCreditCard();
	}

}

CreditCardEntry
=========

# Version History

###4/2/2013
 - Initial Commit

###5/11/2013
 - Updated Demo added screenshots

###4/7/2015
 - Made zip code optional
 - added callback when data entry is complete and card is valid
 - optionally specify helper text color in XML attrs

###4/8/2015
 - Always return CreditCard object when requested even when invalid


# Introduction

This library provides an elegant form for credit card entry that can be easily added to a activity or fragment. Regex is used to validate credit card types and a Luhn check is performed on the card numbers. This form was inspired by the credit entry UI on Square.

Smooth interface

![][1]

Identifies credit card type

![][2]

Hides number for privacy

![][3]

# Using the library

Please see below for an example. Delegate calls for valid entry and accessors for the data will be added in future updates. Adding the form is simply as seen below, or it can be added through an Android layout file.

    public class MainActivity extends Activity {
  
      private LinearLayout linearLayout;
      private CreditCardForm form;
  
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
    
[1]: https://raw.github.com/jgrana/CreditCardEntry/master/screenshot1.png "Screenshot 1"
[2]: https://raw.github.com/jgrana/CreditCardEntry/master/screenshot2.png "Screenshot 2"
[3]: https://raw.github.com/jgrana/CreditCardEntry/master/screenshot3.png "Screenshot 3"

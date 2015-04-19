CreditCardEntry
=========

# Introduction

This library provides an elegant form for credit card entry that can be easily added to a activity or fragment. Regex is used to validate credit card types and a Luhn check is performed on the card numbers. This form was inspired by the credit entry UI on Square.

 - Smooth interface
 - Identifies credit card type
 - Hides number for privacy
 - Supports VISA, MasterCard, Discover and AMEX

![][1]


# Including in your project

Add [jitpack.io] to the end of your `repositories`

```
    repositories {

        ...

        maven { url "https://jitpack.io" }
    }
```

Add the project to your `dependencies`

```
    dependencies {

        ...

        compile 'com.github.zumper:CreditCardEntry:838f228a0a4e6bf7d2e56f43e265490d2b491e28'
    }
```

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


# TODO

  - Diner's Club is not yet implemented, although the assets and regex seem to be in place
    
[1]: https://raw.github.com/zumper/CreditCardEntry/master/demo.gif "Demo GIF"

# Version History

###4/2/2013
 - Initial Commit

###5/11/2013
 - Updated Demo added screenshots

###4/7/2015
 - Made zip code optional (app:include_zip="false")
 - added callback when data entry is complete and card is valid
 - optionally specify helper text color in XML attrs

###4/8/2015
 - Always return CreditCard object when requested even when invalid

###4/18/2015
 - Made helper optional (app:include_helper="false")
 - Fixed a ton of lint warnings
 - Fixed vertical alignment of form fields
 - Fixed over excited animation scroll
 - Last 4 of card now tappable to enter edit mode
 - updated image assets

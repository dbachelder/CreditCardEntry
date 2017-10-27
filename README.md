CreditCardEntry
=========

[![Join the chat at https://gitter.im/dbachelder/CreditCardEntry](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/dbachelder/CreditCardEntry?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-CreditCardEntry-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1771)

# Introduction

This library provides an elegant form for credit card entry that can be easily added to a activity or fragment.
Regex is used to validate credit card types and a Luhn check is performed on the card numbers. This form was inspired by the credit entry UI on Square.

 - Smooth interface
 - Identifies credit card type
 - Hides number for privacy
 - Supports VISA, MasterCard, Discover and AMEX

![][1]


# Including in your project

This project uses [JitPack](https://jitpack.io) to build and release.

Add JitPack to the end of your `repositories`

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

        compile 'com.github.dbachelder:CreditCardEntry:1.4.7'
    }
```

# Using the library

Please see below for an example.

XML

```
        <com.devmarvel.creditcardentry.library.CreditCardForm
            android:id="@+id/credit_card_form"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:helper_text_color="@color/yellow_500"
            app:include_helper="false"
            app:include_zip="false"/>

```

 * `app:text_color` - change the input field's text color (`Color.BLACK` by default).
 * `app:hint_text_color` - change the input field's hint text color (`Color.LTGRAY` by default).
 * `app:cursor_color` - change the input field's cursor color (`Color.BLACK` by default).
 * `app:default_text_colors` - If true, use text colors provided by the app's theme instead of the
   values provided by `app:text_color`,`app:hint_text_color`, and `app:cursor_color`. This overrides
   the values for those three text colors and causes the text inputs to use the colors provided by
   the application's theme.
 * `app:include_helper` - boolean to show/hide the helper text under the widget (`true` by default (i.e. helper is shown))
 * `app:helper_text_color` - change the text color of the hints that appear below the widget by default.
 * `app:include_zip` - boolean to show/hide the zip code in the form (`true` by default (i.e. zip is shown))
 * `app:include_exp` - boolean to show/hide the exp in the form (`true` by default (i.e. exp is shown))
 * `app:include_security` - boolean to show/hide the security code in the form (`true` by default (i.e. security is shown))
 * `app:card_number_hint` - string to put in as a placeholder (hint) in the credit card number field
 * `app:input_background` - the drawable to use as a background (defaults to white square with black 1px border)


In code:

```
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
```

# TODO

  - Diner's Club is not yet implemented, although the assets and regex seem to be in place

# Version History

###7/25/2017
 - added InterSwitch Verve card image
 - added InterSwitch Verve Regex to Card Enum

###3/30/2016
 - made the CreditCardForm work in RTL layout 
 - added getHelperText/setHelperText to CreditEntryFieldBase
 - added setting hint & helper texts to fields

###9/17/2015
 - Changed how the ZipCodeText is validating zip codes so it will work with other countries.
 - fix for `isCreditCardValid()` returning `true` when it is not

###9/8/2015
 - fix bug where isValidCard would be called many times
 - allow for using colors from theme

###8/21/2015
 - don't drop key strokes after backspace

###8/21/2015
 - focus change behavior can now keep up with very fast typing
 - several minor performance tweaks

###8/18/2015
 - fix typo in hint for exp date
 - fix for dates not being able to exceed 2034
 - fix for current month being invalid

###7/27/2015
 - fix occasional NPE

###7/15/2015
 - fix for order of operations when invalidating card and calling delegate methods

###7/07/2015
 - Be able to change input field's text, hint and cursor color

###5/16/2015
 - use an animator to do the scroll

###5/14/2015
 - don't call complete callback twice

###5/13/2015
 - add setters for other CC fields
 - correctly manage state such that more than one form on a screen can handle state change

###5/11/2015
 - Added clearForm()

###4/27/2015
 - removed a bunch of dead resources reducing aar size by ~17%

###4/26/2015
 - fixed corner radius of card back
 - allow setting an invalid number programmatically

###4/24/2015
 - Done IME causes validation check and keyboard dismiss
 - All fields except credit card # are now optional
 - Allow setting credit card number programmatically

###4/21/2015
 - Don't focus credit card by default. Add mechanism for clients to focus any field if desired.
 - CardType now contains image drawable ids for front and back of card
 - The credit card placeholder hint can now be specified via xml
 - Setting focus change listener now delegates to all internal fields.
 - Background of widget is now configurable

###4/20/2015
 - Flip the card image back to the front after CVV field loses focus
 - Expose CardType on CreditCard object
 - Transferred repo ownership

###4/19/2015
 - Don't scroll when scrolling is already happening

###4/18/2015
 - Made helper optional (app:include_helper="false")
 - Fixed a ton of lint warnings
 - Fixed vertical alignment of form fields
 - Fixed over excited animation scroll
 - Last 4 of card now tappable to enter edit mode
 - updated image assets

###4/8/2015
 - Always return CreditCard object when requested even when invalid

###4/7/2015
 - Made zip code optional (app:include_zip="false")
 - added callback when data entry is complete and card is valid
 - optionally specify helper text color in XML attrs

###5/11/2013
 - Updated Demo added screenshots

###4/2/2013
 - Initial Commit

[1]: https://raw.github.com/dbachelder/CreditCardEntry/master/demo.gif "Demo GIF"

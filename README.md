CreditCardEntry
=========

# Version History

4/2/2013 - Initial Commit

# Introduction

This library provides an elegant form for credit card entry that can be easily added to a activity or fragment. Regex is used to validate credit card types and a Luhn check is performed on the card numbers. This form was inspired by the credit entry UI on Square.

# Using the library

Please see below for an example. Delegate calls for valid entry and accessors for the data will be added in future updates. Adding the form is simply as seen below, or it can be added through an Android layout file.

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

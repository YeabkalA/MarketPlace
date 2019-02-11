package com.example.yeabkalwubshit.marketplace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPhoneNumber;
    private EditText mLine1;
    private EditText mLine2;
    private EditText mCity;
    private Spinner mState;
    private EditText mCountry;
    private EditText mZip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mEmail = findViewById(R.id.signUpEmail);
        mFirstName = findViewById(R.id.signUpFirstName);
        mLastName = findViewById(R.id.signUpLastName);
        mPhoneNumber = findViewById(R.id.signUpPhoneNumber);
        mLine1 = findViewById(R.id.signUpLine1);
        mLine2 = findViewById(R.id.signUpLine2);
        mCity = findViewById(R.id.signUpCity);
        mState = findViewById(R.id.signUpState);
        mZip = findViewById(R.id.signUpZip);

    }

}

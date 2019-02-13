package com.example.yeabkalwubshit.marketplace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private EditText mZip;
    private Button mCreateAccountButton;

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
        mCreateAccountButton = findViewById(R.id.createAccountButton);

        // Set up the state selection spinner.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_codes,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mState.setAdapter(adapter);
    }

    Address createAddress() {
        //TODO(yeabkal) use this method in the on-click listener for the `mCreateAccountButton`.
        String line1 = mLine1.getText().toString();
        String line2 = mLine2.getText().toString();
        String city = mCity.getText().toString();
        String state = mState.getSelectedItem().toString();
        String country = getString(R.string.country_usa);
        String zip = mZip.getText().toString();

        Address address = new Address.Builder()
                .setLine1(line1)
                .setLine2(line2)
                .setCity(city)
                .setCountry(country)
                .setState(state)
                .setZip(zip)
                .build();
        if(address.isValid()) {
            return address;
        }
        return null;
    }

}

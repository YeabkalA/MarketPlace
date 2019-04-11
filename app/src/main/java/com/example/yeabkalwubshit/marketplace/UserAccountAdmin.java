package com.example.yeabkalwubshit.marketplace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class UserAccountAdmin extends AppCompatActivity {

    private TextView mFirstLastNameDisplay;
    private TextView mEmailDisplay;
    private TextView mPhoneNumberDisplay;
    private TextView mCityStateDisplay;

    private EditText mEditFirstName;
    private EditText mEditLastName;
    private EditText mEditPhoneNumber;
    private EditText mEditCity;
    private EditText mEditZip;
    private Spinner mEditState;

    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_admin);

        initUI();
        displayData();
    }

    void initUI() {
        mFirstLastNameDisplay = findViewById(R.id.adminFirstLastName);
        mEmailDisplay = findViewById(R.id.adminEmail);
        mPhoneNumberDisplay = findViewById(R.id.adminPhone);
        mCityStateDisplay = findViewById(R.id.adminCityState);

        mEditFirstName = findViewById(R.id.adminEditFirstName);
        mEditLastName = findViewById(R.id.adminEditLastName);
        mEditPhoneNumber = findViewById(R.id.adminEditPhoneNumber);
        mEditCity = findViewById(R.id.adminEditCity);
        mEditZip = findViewById(R.id.adminEditZip);
        mEditState = findViewById(R.id.adminEditState);

        // Set up the state selection spinner.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_codes,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mEditState.setAdapter(adapter);
    }

    void displayData() {
        mFirstLastNameDisplay.setText(user.getFirstName() + " " + user.getLastName());
        mEmailDisplay.setText(user.getEmail());
        mPhoneNumberDisplay.setText(user.getPhoneNumber());
        mCityStateDisplay.setText(user.getAddress().getCity() + ", " + user.getAddress().getState());

        mEditFirstName.setText(user.getFirstName());
        mEditLastName.setText(user.getLastName());
        mEditPhoneNumber.setText(user.getPhoneNumber());
        mEditCity.setText(user.getAddress().getCity());
        mEditZip.setText(user.getAddress().getZip());
    }
}

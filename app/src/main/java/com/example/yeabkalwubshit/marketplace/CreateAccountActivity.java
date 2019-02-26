package com.example.yeabkalwubshit.marketplace;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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
    private EditText mNewPassword;
    private EditText mNewPasswordAgain;
    private Button mCreateAccountButton;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        ActionBar actionBar = getSupportActionBar();
        int actionBarColor = Color.rgb(40,60,250);
        int darkerColor = Color.rgb(10,30,200);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
        setTitle("Create Account");
        getWindow().setStatusBarColor(darkerColor);


        mEmail = findViewById(R.id.signUpEmail);
        mFirstName = findViewById(R.id.signUpFirstName);
        mLastName = findViewById(R.id.signUpLastName);
        mPhoneNumber = findViewById(R.id.signUpPhoneNumber);
        mLine1 = findViewById(R.id.signUpLine1);
        mLine2 = findViewById(R.id.signUpLine2);
        mCity = findViewById(R.id.signUpCity);
        mState = findViewById(R.id.signUpState);
        mZip = findViewById(R.id.signUpZip);
        mNewPassword = findViewById(R.id.newPassword);
        mNewPasswordAgain = findViewById(R.id.newPasswordAgain);
        mCreateAccountButton = findViewById(R.id.createAccountButton);

        // Set up the state selection spinner.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_codes,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mState.setAdapter(adapter);

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validInputs = checkInputs();
                if(!validInputs) {
                    return;
                }
                final User user = createUser();
                if(user != null && user.isValid()) {
                    System.out.println(user.createMap());
                    // TODO(yeabkal) try auth new user.
                    String password1 = mNewPassword.getText().toString();
                    String password2 = mNewPasswordAgain.getText().toString();
                    if(PasswordUtil.validatePassword(password1, password2)) {
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.createUserWithEmailAndPassword(user.getEmail(), password1)
                                .addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {
                                            FirebaseUser currentUser = task.getResult().getUser();
                                            myRef.child("users").child(currentUser.getUid())
                                                    .setValue(user.createMap());
                                            Toast.makeText(getApplicationContext(),
                                                    "Successfully created account for "
                                                            + user.getEmail(),
                                                    Toast.LENGTH_LONG).show();
                                            goToHomepage();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "Authentication Failed",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                        );
                    }
                }
            }
        });
    }

    boolean checkInputs() {
        boolean validInputs = true;

        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String email = mEmail.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();

        String line1 = mLine1.getText().toString();
        String line2 = mLine2.getText().toString();
        String city = mCity.getText().toString();
        String state = mState.getSelectedItem().toString();
        String zip = mZip.getText().toString();

        String password1 = mNewPassword.getText().toString();
        String password2 = mNewPasswordAgain.getText().toString();

        if(TextUtils.isEmpty(firstName)) {
            mFirstName.setError("First name is a required field.");
            validInputs = false;
        }

        if(TextUtils.isEmpty(lastName)) {
            mLastName.setError("Last name is a required field.");
            validInputs = false;
        }

        if(TextUtils.isEmpty(email)) {
            mEmail.setError("Email is a required field.");
            validInputs = false;
        } else {
            boolean isValidEmail = EmailUtil.validateEmail(email);
            if(!isValidEmail) {
                mEmail.setError("Enter a valid email address.");
                validInputs = false;
            }
        }

        if(TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumber.setError("Phone number is a required field.");
            validInputs = false;
        } else {
            boolean isValidPhoneNumber = PhoneNumberUtil.validPhoneNumber(phoneNumber);
            if(!isValidPhoneNumber) {
                mPhoneNumber.setError("Enter a valid phone number");
                validInputs = false;
            }
        }

        if(TextUtils.isEmpty(line1)) {
            mLine1.setError("Address line 1 is a required field.");
            validInputs = false;
        }

        if(TextUtils.isEmpty(city)) {
            mCity.setError("City is a required field.");
            validInputs = false;
        }

        if(TextUtils.isEmpty(state)) {
            validInputs = false;
        }

        if(TextUtils.isEmpty(zip)) {
            mZip.setError("Zip code is a required field.");
            validInputs = false;
        }

        if(TextUtils.isEmpty(password1)) {
            mNewPassword.setError("Password is required");
        } else {
            if(password1.length() < 4) {
                mNewPassword.setError("Password must be at least 4 characters long.");
            } else {
                if(!PasswordUtil.validatePassword(password1, password2)) {
                    mNewPasswordAgain.setError("New passwords should match.");
                }
            }
        }

        return validInputs;
    }

    User createUser() {
        Address address = createAddress();
        if(address == null) { // Invalid address.
            return null;
        }

        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String email = mEmail.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();

        User user = new User.Builder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setAddress(address)
                .setEmail(email)
                .setPhoneNumber(phoneNumber)
                .build();
        return user;
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

    private void goToHomepage() {
        Intent intent = new Intent(CreateAccountActivity.this,
                LoginActivity.class);
        startActivity(intent);
    }
}

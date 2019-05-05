package com.example.yeabkalwubshit.marketplace;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText mUserName;
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
    private Button mUploadImageButton;
    private TextView mImageDesc;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private Uri filePath;

    FirebaseAuth mAuth;

    FirebaseStorage firebaseStorage;
    StorageReference ref;

    private DateUtil dateUtil;

    private final int PICK_IMAGE_REQUEST = 71;

    private boolean imageUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setTitle("Create Account");

        final ActionBar actionBar = getSupportActionBar();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;; i++) {
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {}
                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(i,i,i)));
                }
            }
        });
        thread.start();

        dateUtil = new DateUtil();

        firebaseStorage = FirebaseStorage.getInstance();
        ref = firebaseStorage.getReference();


        mUserName = findViewById(R.id.signUpUserName);
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
        mUploadImageButton = findViewById(R.id.uploadImageBtnAcc);
        mImageDesc = findViewById(R.id.imageUploadStatusCreateAccount);

        mUploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath == null) {
                    chooseImage();
                } else {
                    filePath = null;
                    mUploadImageButton.setText("Upload Image");
                    mImageDesc.setText("");
                }
            }
        });

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
                    String todayStr = dateUtil.today();
                    user.setCreatedOn(todayStr);
                    System.out.println(user.createMap());
                    // TODO(yeabkal) try auth new user.
                    String password1 = mNewPassword.getText().toString();
                    String password2 = mNewPasswordAgain.getText().toString();
                    if(PasswordUtil.validatePassword(password1, password2)) {
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.createUserWithEmailAndPassword(user.getEmail(), password1)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        FirebaseUser currentUser = authResult.getUser();
                                        uploadImage(CreateAccountActivity.this, currentUser.getUid(), user);
                                        System.out.println ("What is going on? " + user.getImageUrl());
                                        System.out.println(user.createMap() + "<<<<<");
                                        myRef.child("users").child(currentUser.getUid())
                                                .setValue(user.createMap());


                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String displayName = mUserName.getText().toString();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(displayName).build();

                                        user.updateProfile(profileUpdates);


                                        Toast.makeText(getApplicationContext(),
                                                "Successfully created account for "
                                                        + user.getEmail(),
                                                Toast.LENGTH_LONG).show();
                                        goToHomepage();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        e.getMessage(),
                                        Toast.LENGTH_LONG).show();

                            }
                        });
                    } else {
                        mNewPassword.setError("Passwords must match.");
                    }
                }
            }
        });
    }

    boolean checkInputs() {
        boolean validInputs = true;

        String userName = mUserName.getText().toString();
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

        if(TextUtils.isEmpty(userName)) {
            mUserName.setError("User name is a required field.");
            validInputs = false;
        }
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

        if(TextUtils.isEmpty(zip) || !Address.isValidZipCode(zip)) {
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

        String userName = mUserName.getText().toString();
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String email = mEmail.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();

        User user = new User.Builder()
                .setUserName(userName)
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    private void goToHomepage() {
        Intent intent = new Intent(CreateAccountActivity.this,
                LoginActivity.class);
        startActivity(intent);
    }

    private void uploadImage(Activity act, String imageId, User user) {
        if(filePath != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setView(R.xml.progress);
            final Dialog dialog = builder.create();
            dialog.show();
            dialog.setTitle("Creating your account...");

            String imageStoragePath = getImagePathString(imageId);
            user.setImageURL(imageStoragePath);
            System.out.println("Set image url of user to " + imageStoragePath);

            ref.child(imageStoragePath).putFile(filePath)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(CreateAccountActivity.this, "Please try again later",
                            Toast.LENGTH_LONG);

                }
            });
        } else {
            Toast.makeText(CreateAccountActivity.this, "NO ITEM", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            mImageDesc.setText("Image uploaded: " + filePath.getPath());
            mUploadImageButton.setText("Remove Image");
        }
    }

    private String getImagePathString(String userId) {
        return "images/users/" + userId;
    }
}

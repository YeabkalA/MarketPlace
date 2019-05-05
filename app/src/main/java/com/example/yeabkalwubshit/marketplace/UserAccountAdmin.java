package com.example.yeabkalwubshit.marketplace;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

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

    private ImageView mProfilePic;
    private Spinner mEditState;

    private Button mChangeInfo;
    private Button mUploadImageButton;

    public static User user;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_admin);

        setTitle("Profile");
        initUI();
        displayData(user);
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
        mProfilePic = findViewById(R.id.adminProfilePicture);
        mChangeInfo = findViewById(R.id.changeAccountDetails);

        mUploadImageButton = findViewById(R.id.updateImage);
        mUploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath == null) {
                    chooseImage();
                } else {
                    filePath = null;
                    mUploadImageButton.setText("Upload Image");
                }
            }
        });

        // Set up the state selection spinner.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_codes,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mEditState.setAdapter(adapter);

        mChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = mEditFirstName.getText().toString();
                String lastName = mEditLastName.getText().toString();
                String zipCode = mEditZip.getText().toString();
                String city = mEditCity.getText().toString();
                String state = mEditState.getSelectedItem().toString();
                String phone = mEditPhoneNumber.getText().toString();

                UserAccountDetailChangeRequest changeRequest = new UserAccountDetailChangeRequest()
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setZipCode(zipCode)
                        .setCity(city)
                        .setState(state)
                        .setPhoneNumber(phone);
                Error isValidRequest = changeRequest.isValidRequest();
                if(isValidRequest != null) {
                    Toast.makeText(getApplicationContext(), isValidRequest.getLocalizedMessage()
                    , Toast.LENGTH_LONG).show();
                } else {
                    NetworkServiceHandler networkServiceHandler = NetworkServiceHandler.getInstance();
                    networkServiceHandler.updateAccountInfo(changeRequest);
                    networkServiceHandler.uploadImage(UserAccountAdmin.this, null, filePath);
                    Toast.makeText(getApplicationContext(), "Successfully updated account info!"
                            , Toast.LENGTH_LONG).show();
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(networkServiceHandler.getCurrentUsersId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    HashMap<String, Object> data = (HashMap) dataSnapshot.getValue();
                                    User user = new User();
                                    user.populateFromMap(data);
                                    displayData(user);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        });
    }

    void displayData(User user) {
        mFirstLastNameDisplay.setText(user.getFirstName() + " " + user.getLastName());
        mEmailDisplay.setText(user.getEmail());
        mPhoneNumberDisplay.setText(user.getPhoneNumber());
        mCityStateDisplay.setText(user.getAddress().getCity() + ", " + user.getAddress().getState());

        mEditFirstName.setText(user.getFirstName());
        mEditLastName.setText(user.getLastName());
        mEditPhoneNumber.setText(user.getPhoneNumber());
        mEditCity.setText(user.getAddress().getCity());
        mEditZip.setText(user.getAddress().getZip());

        System.out.println("Got the following image url " + user.getImageUrl());
        if(user.getImageUrl() != null && !user.getImageUrl().equals("")) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference mStorageRef = storage.getReference();
            StorageReference islandRef = mStorageRef.child(user.getImageUrl());
            Task<Uri> task = islandRef.getDownloadUrl();
            while(!task.isComplete()) {}

            String url = task.getResult().toString();
            Picasso.get()
                    .load(url)
                    .fit()
                    .centerCrop()
                    .into(mProfilePic);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            mUploadImageButton.setText("Remove Image");
        }
    }
}

package com.example.yeabkalwubshit.marketplace;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class PostItemActivity extends AppCompatActivity {

    private EditText mDescription;
    private EditText mTitle;
    private EditText mPrice;

    private Button mPostBtn;
    private RadioButton mNewBtn;
    private RadioButton mUsedBtn;
    private Button mUploadImageBtn;

    private ImageView mImage;
    private Uri filePath;
    private TextView mImageDesc;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage firebaseStorage;
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        ActionBar actionBar = getSupportActionBar();
        int actionBarColor = Color.rgb(40, 150, 8);
        int darkerColor = Color.rgb(30, 145, 8);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
        setTitle("Add Item");
        getWindow().setStatusBarColor(darkerColor);

        firebaseStorage = FirebaseStorage.getInstance();
        ref = firebaseStorage.getReference();

        initUI();

        mUploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference mDbRef = mDatabase.getReference();
                mDbRef.child("items")
                        .child("next_item_id")
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot != null) {
                                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                            Long nextId = (Long) dataSnapshot.getValue();

                                            Item item = setItem(nextId);
                                            if(item != null) {
                                                String uid = mAuth.getUid();
                                                item.setOwnerId(uid);
                                                mDbRef.child("items")
                                                        .child("next_item_id")
                                                        .setValue(nextId + 1);
                                                DateUtil dateUtil = new DateUtil();
                                                String todayStr = dateUtil.today();
                                                item.setPostedOn(todayStr);
                                                String imageUrl = getImagePathString(Long.toString(nextId));
                                                if(filePath != null) {
                                                    item.setImageURL(imageUrl);
                                                }
                                                mDbRef.child("items").child(item.getId())
                                                        .setValue(item.createMap());
                                                mDbRef.child("users").child(uid).child("items")
                                                        .child(Long.toString(nextId))
                                                        .setValue(todayStr);
                                                uploadImage(PostItemActivity.this, imageUrl);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );
            }
        });


    }

    private void initUI() {
        mDescription = findViewById(R.id.itemDescription);
        mTitle = findViewById(R.id.itemTitle);
        mPrice = findViewById(R.id.itemPriceTag);
        mPostBtn = findViewById(R.id.postItem);
        mNewBtn = findViewById(R.id.newBtn);
        mUsedBtn = findViewById(R.id.usedBtn);
        mUsedBtn.setChecked(true);
        mUploadImageBtn = findViewById(R.id.uploadImageBtn);
        mImage = findViewById(R.id.img);
        mImageDesc = findViewById(R.id.imageDesc);
    }

    Item setItem(Long id) {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String priceInCentsStr = mPrice.getText().toString();

        if(TextUtils.isEmpty(title)) {
            mTitle.setError("Title is a required field");
            return null;
        }

        if(TextUtils.isEmpty(description)) {
            mDescription.setError("Description is a required field.");
            return null;
        }

        if(TextUtils.isEmpty(priceInCentsStr)) {
            mPrice.setError("Price is a required field");
            return null;
        }

        String condition = mNewBtn.isChecked() ? "NEW" : "USED";

        double priceInCents;

        try {
            priceInCents = 100 * Double.parseDouble(priceInCentsStr);
        } catch(Exception e) {
            mPrice.setError("Enter numeric values only.");
            return null;
        }

        Item item = new Item.Builder()
                .setTitle(title)
                .setDescription(description)
                .setPriceInCents((long) priceInCents)
                .setId(Long.toString(id))
                .setCondition(condition)
                .build();

        return item;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    private void uploadImage(Activity act, String imageUrl) {
        if(filePath != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setView(R.xml.progress);
            final Dialog dialog = builder.create();
            dialog.show();
            dialog.setTitle("Creating your item...");

            ref.child(imageUrl).putFile(filePath)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            Toast.makeText(PostItemActivity.this, "Created Item",
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(PostItemActivity.this, "Please try again later",
                            Toast.LENGTH_LONG);

                }
            });
        } else {
            Toast.makeText(PostItemActivity.this, "NO ITEM", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            mImageDesc.setText("Image uploaded");
//            try {
////                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
////                mImage.setImageBitmap(bitmap);
//                mImageDesc.setText("Image uploaded");
//            } catch(IOException e) {
//                e.printStackTrace();
//            }
        }

    }

    private String getImagePathString(String itemId) {
        return "images/" + itemId;
    }
}

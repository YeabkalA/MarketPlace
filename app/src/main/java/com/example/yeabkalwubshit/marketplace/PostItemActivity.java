package com.example.yeabkalwubshit.marketplace;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostItemActivity extends AppCompatActivity {

    private EditText mDescription;
    private EditText mTitle;
    private EditText mPrice;

    private Button mPostBtn;
    private RadioButton mNewBtn;
    private RadioButton mUsedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        initUI();

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
                                                mDbRef.child("items").child(item.getId())
                                                        .setValue(item.createMap());
                                                mDbRef.child("users").child(uid).child("items")
                                                        .child(Long.toString(nextId))
                                                        .setValue(todayStr);
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
    }

    boolean checkInputs() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String priceInCentsStr = mPrice.getText().toString();

        if(TextUtils.isEmpty(title)) {
            mTitle.setError("Title is a required field");
            return false;
        }

        if(TextUtils.isEmpty(description)) {
            mDescription.setError("Description is a required field.");
            return false;
        }

        if(TextUtils.isEmpty(priceInCentsStr)) {
            mPrice.setError("Price is a required field");
            return false;
        }

        if(!mUsedBtn.isChecked() && mNewBtn.isChecked()) {
            return false;
        }

        double priceInCents;

        try {
            priceInCents = 100 * Double.parseDouble(priceInCentsStr);
        } catch(Exception e) {
            mPrice.setError("Enter numeric values only.");
            return false;
        }
        return true;
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
}

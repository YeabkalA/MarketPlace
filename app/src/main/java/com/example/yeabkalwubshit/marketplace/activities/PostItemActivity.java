package com.example.yeabkalwubshit.marketplace.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yeabkalwubshit.marketplace.tools.DateUtil;
import com.example.yeabkalwubshit.marketplace.R;
import com.example.yeabkalwubshit.marketplace.objects.Item;
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

import java.util.ArrayList;
import java.util.HashMap;

public class PostItemActivity extends AppCompatActivity {

    private EditText mDescription;
    private EditText mTitle;
    private EditText mPrice;

    private Button mPostBtn;
    private RadioButton mNewBtn;
    private RadioButton mUsedBtn;
    private Button mUploadImageBtn;
    private Button mRemoveImagesBtn;

    private ArrayList<Uri> filePaths;

    private Spinner mCategory;
    private ImageView[] checkBoxes;
    private TextView[] imageDescs;

    private String[] imageUrlMemo = {"", "", ""};

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage firebaseStorage;
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);
        setTitle("Add Item");

        firebaseStorage = FirebaseStorage.getInstance();
        ref = firebaseStorage.getReference();

        initUI();

        // Set up the state selection spinner.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCategory.setAdapter(adapter);

        mUploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePaths != null && filePaths.size()==3) {
                    Toast.makeText(getApplicationContext(),
                            "You cannot upload more than 3 images.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                chooseImage();
            }
        });

        mRemoveImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImages();
                updateImageDescAndCheckBoxes();
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
                                                String userName = mAuth.getCurrentUser().getDisplayName();
                                                item.setOwnerId(uid);
                                                item.setOwnerUserName(userName);
                                                mDbRef.child("items")
                                                        .child("next_item_id")
                                                        .setValue(nextId + 1);
                                                DateUtil dateUtil = new DateUtil();
                                                String todayStr = dateUtil.today();
                                                item.setPostedOn(todayStr);
                                                String itemId = Long.toString(nextId);

                                                for(int i=0; i<filePaths.size(); i++) {
                                                    item.addImageUrl(getImagePathString(itemId, i));
                                                }

//                                                if(filePath != null) {
//                                                    item.setImageURL(imageUrl);
//                                                }
                                                mDbRef.child("items").child(item.getId())
                                                        .setValue(item.createMap());
                                                HashMap<String, String> itemBasicInfo =
                                                        new HashMap<>();
                                                itemBasicInfo.put("title", item.getTitle());
                                                itemBasicInfo.put("id", item.getId());
                                                mDbRef.child("users").child(uid).child("items")
                                                        .child(Long.toString(nextId))
                                                        .setValue(itemBasicInfo);
                                                uploadImage(PostItemActivity.this, itemId);

                                                String successText = "Successfully created item - " + item.getTitle();
                                                Toast.makeText(PostItemActivity.this, successText,
                                                        Toast.LENGTH_LONG).show();
                                                openFeeds();
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
        filePaths = new ArrayList<>();
        mDescription = findViewById(R.id.itemDescription);
        mTitle = findViewById(R.id.itemTitle);
        mPrice = findViewById(R.id.itemPriceTag);
        mPostBtn = findViewById(R.id.postItem);
        mNewBtn = findViewById(R.id.newBtn);
        mUsedBtn = findViewById(R.id.usedBtn);
        mUsedBtn.setChecked(true);
        mUploadImageBtn = findViewById(R.id.uploadImageBtn);
        mRemoveImagesBtn = findViewById(R.id.removeImageBtn);
        mCategory = findViewById(R.id.itemCategory);

        mRemoveImagesBtn.setVisibility(View.GONE);

        checkBoxes = new ImageView[3];
        checkBoxes[0] = findViewById(R.id.check1);
        checkBoxes[1] = findViewById(R.id.check2);
        checkBoxes[2] = findViewById(R.id.check3);

        for(ImageView checkBox: checkBoxes) {
            checkBox.setVisibility(View.GONE);
        }

        imageDescs = new TextView[3];
        imageDescs[0] = findViewById(R.id.imageDesc1);
        imageDescs[1] = findViewById(R.id.imageDesc2);
        imageDescs[2] = findViewById(R.id.imageDesc3);

        for(TextView imageDesc: imageDescs) {
            imageDesc.setVisibility(View.GONE);
        }

    }

    Item setItem(Long id) {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String priceInCentsStr = mPrice.getText().toString();
        String category = mCategory.getSelectedItem().toString();

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

        if(TextUtils.isEmpty(category)) {
            Toast.makeText(getApplicationContext(),
                    "Select at least one category for your new item.",
                    Toast.LENGTH_LONG).show();
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

        if(filePaths == null) {
            Toast.makeText(getApplicationContext(),
                    "Please, select image for your item",
                    Toast.LENGTH_LONG).show();
            return null;
        }

        Item item = new Item.Builder()
                .setTitle(title)
                .setDescription(description)
                .setPriceInCents((long) priceInCents)
                .setId(Long.toString(id))
                .setCondition(condition)
                .setCategory(category)
                .build();

        return item;
    }

    private void openFeeds() {
        Intent intent = new Intent(PostItemActivity.this, Feed.class);
        startActivity(intent);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    private void removeImages() {
        filePaths = null;
        mRemoveImagesBtn.setVisibility(View.GONE);
    }

    private void updateImageDescAndCheckBoxes() {
        if(filePaths == null || filePaths.size() == 0) {
            for(int i=0; i<3; i++) {
                imageDescs[i].setVisibility(View.GONE);
                checkBoxes[i].setVisibility(View.GONE);
            }
            return;
        }
        int i = 0;
        for(i=0; i < filePaths.size(); i++) {
            imageDescs[i].setVisibility(View.VISIBLE);
            imageDescs[i].setText(filePaths.get(i).getPath());
            checkBoxes[i].setVisibility(View.VISIBLE);
        }
        while(i < 3) {
            imageDescs[i].setVisibility(View.GONE);
            checkBoxes[i].setVisibility(View.GONE);
            i++;
        }
    }

    private void uploadImage(Activity act, String itemID) {
        if(filePaths != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setView(R.xml.progress);
            final Dialog dialog = builder.create();
            dialog.show();
            dialog.setTitle("Creating your item...");

            for(int i=0; i<filePaths.size(); i++) {
                Uri filePath = filePaths.get(i);
                String imageUrl = getImagePathString(itemID, i);
                Task task = ref.child(imageUrl).putFile(filePath);

                try {
                    while(!task.isComplete()) {
                        Thread.sleep(10);
                    }
                    task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                } catch (Exception e){}

            }

        } else {
            Toast.makeText(PostItemActivity.this, "NO ITEM", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            if(filePaths == null) filePaths = new ArrayList<>();
            filePaths.add(filePath);
            updateImageDescAndCheckBoxes();
            mRemoveImagesBtn.setVisibility(View.VISIBLE);
        }

    }



    private String getImagePathString(String itemId, int ind) {
        if(imageUrlMemo[ind].equals("")) {
            String path = String.format("/images/%s/%d", itemId, ind);
            imageUrlMemo[ind] = path;
            return path;
        } else {
            return imageUrlMemo[ind];
        }
    }
}

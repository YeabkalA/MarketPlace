package com.example.yeabkalwubshit.marketplace;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ItemDetailView extends Activity {

    static Item item;

    private TextView mDescription;
    private TextView mPosterInfo;
    private TextView mCategory;
    private TextView mNewUsed;
    private RatingBar mRatingBar;
    private TextView mPostDate;
    private TextView mPrice;
    private TextView mWinningBid;
    private TextView mNumOfBids;
    private ViewPager mViewPager;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_view);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle("Checking how subtitle works");
        toolbar.setTitle(item.getTitle());

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        initUI();
        setUpUI();
    }

    void initUI() {
        mDescription = findViewById(R.id.itemDetailDescription);
        mPosterInfo = findViewById(R.id.itemDetailPoster);
        mNewUsed = findViewById(R.id.itemDetailNewUsed);
        mCategory = findViewById(R.id.itemDetailCategory);
        mRatingBar = findViewById(R.id.itemDetailPosterRating);
        mPostDate = findViewById(R.id.itemDetailPostDate);
        mViewPager = findViewById(R.id.itemDetailViewPager);
        mPrice = findViewById(R.id.itemDetailPrice);
        mWinningBid = findViewById(R.id.itemDetailWinningBid);
        mNumOfBids = findViewById(R.id.itemDetailNumOfBids);
    }

    void setUpUI() {
        mDescription.setText(item.getDescription());
        mPosterInfo.setText("by " + "yeaba");
        mNewUsed.setText(item.getCondition());
        mCategory.setText("Category");
        mRatingBar.setRating(3.0f);
        mPostDate.setText(item.getPostedOn());
        mPrice.setText(Item.getDollarRepresentation(item.getPriceInCents()));
        mNumOfBids.setText("5");
        mWinningBid.setText(Item.getDollarRepresentation(item.getPriceInCents() + 10));

        ArrayList<String> viewPagerList = new ArrayList<>();

        if(!TextUtils.isEmpty(item.getImageURL())) {
           StorageReference islandRef = mStorageRef.child(item.getImageURL());
           Task<Uri> task = islandRef.getDownloadUrl();
           while(!task.isComplete()) {}
           String url = task.getResult().toString();
           viewPagerList.add(url);
       }

        viewPagerList.add("https://d3nfwcxd527z59.cloudfront.net/content/uploads/2019/03/21104030/Francis-Coquelin-Valencia.jpg");

        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), viewPagerList);
        mViewPager.setAdapter(adapter);
    }
}

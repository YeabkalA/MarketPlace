package com.example.yeabkalwubshit.marketplace;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
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
    private Button mBiddingButton;

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

        mBiddingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runBidSumission();
            }
        });
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
        mBiddingButton = findViewById(R.id.biddingButton);
    }

    void setUpUI() {
        mDescription.setText(item.getDescription());
        mPosterInfo.setText("by " + "yeaba");
        mNewUsed.setText(item.getCondition());
        mCategory.setText(item.getCategory() == null ? "Category" : item.getCategory());
        mRatingBar.setRating(3.0f);
        mPostDate.setText("posted on " + item.getPostedOn());
        mPrice.setText(Item.getDollarRepresentation(item.getPriceInCents()));
        if(item.getWinningBid() != null) {
            mWinningBid.setText(Item.getDollarRepresentation(item.getWinningBid().getValueInCents()));
        } else {
            mWinningBid.setText("NA");
        }
        mNumOfBids.setText(Integer.toString(item.getBids().size()));


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

    void runBidSumission() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ItemDetailView.this)
                .setTitle("Bid Submission");
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.bid_dialog, null);
        view.setBackgroundColor(Color.WHITE);
        alertBuilder.setView(view);

        final EditText bidEntryBox = view.findViewById(R.id.bid_value_entry);

        alertBuilder.setPositiveButton("Submit Bid", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double value;
                try {
                    value = Double.parseDouble(bidEntryBox.getText().toString());
                } catch (Exception e) {
                    bidEntryBox.setError("Enter a valid number.");
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid number.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                NetworkServiceHandler networkServiceHandler = NetworkServiceHandler.getInstance();

                Bid bid = new Bid.Builder()
                        .setItemId(item.getId())
                        .setIssuerId(networkServiceHandler.getCurrentUsersId())
                        .setOwnerId(item.getOwnerId())
                        .setValueInCents((long) value * 100)
                        .build();
                networkServiceHandler.bidForItem(item.getId(), bid);
            }
        });
        AlertDialog dialog = alertBuilder.show();

    }
}

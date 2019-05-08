package com.example.yeabkalwubshit.marketplace.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yeabkalwubshit.marketplace.networkhandlers.NetworkServiceHandler;
import com.example.yeabkalwubshit.marketplace.R;
import com.example.yeabkalwubshit.marketplace.adapters.ViewPagerAdapter;
import com.example.yeabkalwubshit.marketplace.objects.Bid;
import com.example.yeabkalwubshit.marketplace.objects.Item;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ItemDetailView extends Activity {

    public static Item item;

    private TextView mTitle;
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

    private NetworkServiceHandler networkServiceHandler = NetworkServiceHandler.getInstance();

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
        mTitle = findViewById(R.id.itemDetailTitle);
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

        String currentUserId = networkServiceHandler.getCurrentUsersId();
        if (item.getOwnerId().equals(currentUserId)) {
            mBiddingButton.setVisibility(View.GONE);
        }
    }

    void setUpUI() {
        mTitle.setText(item.getTitle());
        mDescription.setText(item.getDescription());
        mPosterInfo.setText("by " + "yeaba");
        mNewUsed.setText(item.getCondition());
        mCategory.setText(item.getCategory() == null ? "Category" : item.getCategory());
        mRatingBar.setRating(3.0f);
        mPostDate.setText("posted on " + item.getPostedOn());
        mPrice.setText(Item.getDollarRepresentation(item.getPriceInCents()));
        if (item.getWinningBid() != null) {
            mWinningBid.setText(Item.getDollarRepresentation(item.getWinningBid().getValueInCents()));
        } else {
            mWinningBid.setText("-");
        }
        mNumOfBids.setText(Integer.toString(item.getBids().size()));


        ArrayList<String> viewPagerList = new ArrayList<>();

        if (item.getImageUrls() != null && item.getImageUrls().size() != 0) {
            for (String imageUrl : item.getImageUrls()) {
                StorageReference islandRef = mStorageRef.child(imageUrl);
                Task<Uri> task = islandRef.getDownloadUrl();
                while (!task.isComplete()) {
                }
                String url = task.getResult().toString();
                viewPagerList.add(url);
            }
        }

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
                long valueInCents;
                try {
                    value = Double.parseDouble(bidEntryBox.getText().toString());
                } catch (Exception e) {
                    bidEntryBox.setError("Enter a valid number.");
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid number.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                valueInCents = (long) value * 100;
                if(valueInCents < item.getPriceInCents()) {
                    System.out.println("ValueInCents " + item.getPriceInCents() + " my bid is " + valueInCents);
                    Toast.makeText(getApplicationContext(),
                            "Bid value must atleast be equal to the the item price.",
                            Toast.LENGTH_LONG).show();
                }
                Bid bid = new Bid.Builder()
                        .setItemId(item.getId())
                        .setIssuerId(networkServiceHandler.getCurrentUsersId())
                        .setOwnerId(item.getOwnerId())
                        .setValueInCents(valueInCents)
                        .build();
                networkServiceHandler.bidForItem(bid);
            }
        });
        alertBuilder.show().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }
}

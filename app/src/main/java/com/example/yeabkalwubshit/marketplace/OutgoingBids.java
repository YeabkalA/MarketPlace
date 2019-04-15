package com.example.yeabkalwubshit.marketplace;

import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

public class OutgoingBids extends AppCompatActivity {

    private RecyclerView mOutgoingBidsList;
    private ProgressBar mProgressBar;
    private NetworkServiceHandler networkServiceHandler;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView.Adapter mAdapter;

    private ArrayList<Bid> bids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_bids);

        mOutgoingBidsList = findViewById(R.id.outgoingBidsList);
        mOutgoingBidsList.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        mOutgoingBidsList.setLayoutManager(layoutManager);

        init();
        fetchDataAndSetupAdapter();

    }

    void init() {
        mOutgoingBidsList = findViewById(R.id.outgoingBidsList);
        mProgressBar = findViewById(R.id.outgoingBidsProgress);
        networkServiceHandler = NetworkServiceHandler.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
    }

    void fetchDataAndSetupAdapter() {
        mProgressBar.setVisibility(View.VISIBLE);
        String currentUser = networkServiceHandler.getCurrentUsersId();
        mRef.child("users").child(currentUser).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = new User();
                        user.populateFromMap((HashMap) dataSnapshot.getValue());
                        bids = new ArrayList<>(user.getBids());
                        mAdapter = new OutgoingBidsAdapter(bids, OutgoingBids.this);
                        mOutgoingBidsList.setAdapter(mAdapter);
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

}

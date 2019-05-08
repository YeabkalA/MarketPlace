package com.example.yeabkalwubshit.marketplace.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.yeabkalwubshit.marketplace.objects.ItemStatus;
import com.example.yeabkalwubshit.marketplace.networkhandlers.NetworkServiceHandler;
import com.example.yeabkalwubshit.marketplace.adapters.OutgoingBidsAdapter;
import com.example.yeabkalwubshit.marketplace.R;
import com.example.yeabkalwubshit.marketplace.objects.Bid;
import com.example.yeabkalwubshit.marketplace.objects.Item;
import com.example.yeabkalwubshit.marketplace.objects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        fetchDataAndSetupAdapter(null);

    }

    void init() {
        mOutgoingBidsList = findViewById(R.id.outgoingBidsList);
        mProgressBar = findViewById(R.id.outgoingBidsProgress);
        networkServiceHandler = NetworkServiceHandler.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);

        final String[] stati = {ItemStatus.STATUS_AVAILABLE, ItemStatus.STATUS_FINALIZING, ItemStatus.STATUS_COMPLETED};
        for(int i=0; i<4; i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setVisible(true);
            final int index = i;
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    fetchDataAndSetupAdapter(index != 3 ? stati[index] : null);
                    return true;
                }
            });
        }
        return true;
    }

    void fetchDataAndSetupAdapter(final String status) {
        mProgressBar.setVisibility(View.VISIBLE);
        String currentUser = networkServiceHandler.getCurrentUsersId();
        mRef.child("users").child(currentUser).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = new User();
                        user.populateFromMap((HashMap) dataSnapshot.getValue());
                        bids = new ArrayList<>(user.getBids());

                        if(status != null) {
                            final List<Bid> filteredBids = new ArrayList<>();
                            for(final Bid bid: bids) {
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference mRef = database.getReference();

                                mRef.child("items").child(bid.getItemId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final Item item = new Item();
                                        item.populateFromMap((HashMap) dataSnapshot.getValue());
                                        if(item.getStatus().getStatus().equals(status)) {
                                            filteredBids.add(bid);
                                        }
                                        mAdapter = new OutgoingBidsAdapter(filteredBids, OutgoingBids.this);
                                        mOutgoingBidsList.setAdapter(mAdapter);
                                        mProgressBar.setVisibility(View.GONE);
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        } else {
                            mAdapter = new OutgoingBidsAdapter(bids, OutgoingBids.this);
                            mOutgoingBidsList.setAdapter(mAdapter);
                            mProgressBar.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

}

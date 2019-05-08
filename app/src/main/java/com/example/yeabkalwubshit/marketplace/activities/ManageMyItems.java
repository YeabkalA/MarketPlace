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
import android.widget.Toast;

import com.example.yeabkalwubshit.marketplace.objects.ItemStatus;
import com.example.yeabkalwubshit.marketplace.adapters.ManageMyItemsAdapter;
import com.example.yeabkalwubshit.marketplace.networkhandlers.NetworkServiceHandler;
import com.example.yeabkalwubshit.marketplace.R;
import com.example.yeabkalwubshit.marketplace.objects.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageMyItems extends AppCompatActivity {

    private RecyclerView mItemList;
    private ProgressBar mProgressBar;
    private NetworkServiceHandler networkServiceHandler;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private RecyclerView.LayoutManager layoutManager;

    private HashMap<String, Object> dataOrigin;

    private ManageMyItemsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_my_items);

        mItemList = findViewById(R.id.manageItemsList);
        mItemList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mItemList.setLayoutManager(layoutManager);

        init();
        fetchDataAndSetupAdapter(null);

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

    void init() {
        dataOrigin = new HashMap<>();
        mItemList = findViewById(R.id.manageItemsList);
        mProgressBar = findViewById(R.id.manageItemsProgress);
        networkServiceHandler = NetworkServiceHandler.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
    }

    void fetchDataAndSetupAdapter(final String status) {
        mProgressBar.setVisibility(View.VISIBLE);
        String currentUser = networkServiceHandler.getCurrentUsersId();
        mRef.child("users").child(currentUser).child("items").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, Object> itemsInfo = (HashMap) dataSnapshot.getValue();
                        if(itemsInfo == null) {
                            Toast.makeText(getApplicationContext(),
                                    "You have no items",
                                    Toast.LENGTH_LONG).show();
                            mProgressBar.setVisibility(View.GONE);
                            return;
                        }
                        final ArrayList<Item> items = new ArrayList<>();
                        for (String itemId : itemsInfo.keySet()) {
                            mRef.child("items").child(itemId).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Item item = new Item();
                                            item.populateFromMap((HashMap) dataSnapshot.getValue());
                                            if(status == null || item.getStatus() == null) {
                                                items.add(item);
                                                mAdapter = new ManageMyItemsAdapter(items, ManageMyItems.this);
                                                mItemList.setAdapter(mAdapter);
                                            } else {
                                                if(item.getStatus().equals(status)) {
                                                    items.add(item);
                                                    mAdapter = new ManageMyItemsAdapter(items, ManageMyItems.this);
                                                    mItemList.setAdapter(mAdapter);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    }
                            );
                        }
                        mProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

}

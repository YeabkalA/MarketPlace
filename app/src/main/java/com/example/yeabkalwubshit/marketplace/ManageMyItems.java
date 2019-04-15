package com.example.yeabkalwubshit.marketplace;

import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

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

    private ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_my_items);

        mItemList = findViewById(R.id.manageItemsList);
        mItemList.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        mItemList.setLayoutManager(layoutManager);

        init();
        fetchDataAndSetupAdapter();

    }

    void init() {
        dataOrigin = new HashMap<>();
        mItemList = findViewById(R.id.manageItemsList);
        mProgressBar = findViewById(R.id.manageItemsProgress);
        networkServiceHandler = NetworkServiceHandler.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
    }

    void fetchDataAndSetupAdapter() {
        mProgressBar.setVisibility(View.VISIBLE);
        String currentUser = networkServiceHandler.getCurrentUsersId();
        mRef.child("users").child(currentUser).child("items").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println("Arraylist ?  " + dataSnapshot.toString());
                        HashMap<String, Object> itemsInfo = (HashMap) dataSnapshot.getValue();
                        System.out.println("The itemsInfo " + itemsInfo);
                        items = new ArrayList<>();
                        mAdapter = new ManageMyItemsAdapter(items, ManageMyItems.this);
                        mItemList.setAdapter(mAdapter);
                        if(itemsInfo == null) {
                            Toast.makeText(getApplicationContext(),
                                    "You have no items",
                                    Toast.LENGTH_LONG).show();
                            mProgressBar.setVisibility(View.GONE);
                            return;
                        }
                        for (String itemId : itemsInfo.keySet()) {
                            mRef.child("items").child(itemId).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Item item = new Item();
                                            System.out.println("The snapshot "+ dataSnapshot.toString());
                                            item.populateFromMap((HashMap) dataSnapshot.getValue());
                                            mAdapter.add(mAdapter.getItemCount(), item);
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

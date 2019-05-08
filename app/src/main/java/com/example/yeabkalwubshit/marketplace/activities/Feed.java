package com.example.yeabkalwubshit.marketplace.activities;

import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yeabkalwubshit.marketplace.adapters.FeedListAdapter;
import com.example.yeabkalwubshit.marketplace.tools.ItemSearcher;
import com.example.yeabkalwubshit.marketplace.objects.ItemStatus;
import com.example.yeabkalwubshit.marketplace.networkhandlers.NetworkServiceHandler;
import com.example.yeabkalwubshit.marketplace.R;
import com.example.yeabkalwubshit.marketplace.tools.SampleRecentSuggestionProvider;
import com.example.yeabkalwubshit.marketplace.objects.Item;
import com.example.yeabkalwubshit.marketplace.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Feed extends AppCompatActivity {

    private RecyclerView mFeedList;
    private TextView mFeedNoItemDisplay;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar mProgressBar;

    private static HashMap<String, Object> dataOrigin;
    private ArrayList<Item> items;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initDbItems();
        initUI();
        refreshFeed();

    }

    @Override
    protected void onResume() {
        mFeedList.setAdapter(mAdapter);
        super.onResume();
        initDbItems();
        initUI();
        refreshFeed();
    }

    private void initDbItems() {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
    }
    private void initUI() {
        mFeedList = findViewById(R.id.feedItemsList);
        mFeedList.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        mFeedList.setLayoutManager(layoutManager);
        mFeedNoItemDisplay = findViewById(R.id.feedNoItemDisplay);
        mFeedNoItemDisplay.setVisibility(View.GONE);
        mFeedList = findViewById(R.id.feedItemsList);
        mProgressBar = findViewById(R.id.feedProgressBar);
    }

    private void fillItems(boolean fetchItemFromDataOrigin) {
        mProgressBar.setVisibility(View.VISIBLE);
        if(fetchItemFromDataOrigin) {
            items = new ArrayList<>();
            for(String key : dataOrigin.keySet()) {
                if(key.equals("next_item_id")) continue;
                HashMap<String, Object> itemData = (HashMap) dataOrigin.get(key);
                Item item = new Item();
                item.populateFromMap(itemData);
                if(!item.getStatus().getStatus().equals(ItemStatus.STATUS_AVAILABLE)) {
                    continue;
                }
                items.add(item);
            }
        }
        mAdapter = new FeedListAdapter(items,this);
        mFeedList.setAdapter(mAdapter);
        if(mAdapter.getItemCount() == 0) mFeedNoItemDisplay.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void refreshFeed() {
        mRef.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> data = (HashMap) dataSnapshot.getValue();
                dataOrigin = data;
                fillItems(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        SearchView searchView = (SearchView)menu.getItem(0).getActionView();

        searchView.
                setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        SearchRecentSuggestions suggestions =
                                new SearchRecentSuggestions(getApplicationContext(),
                                        SampleRecentSuggestionProvider.AUTHORITY,
                                        SampleRecentSuggestionProvider.MODE);
                        ItemSearcher searcher = new ItemSearcher(items);
                        items = searcher.runQuery(query, items.size());
                        fillItems(false);
                        suggestions.saveRecentQuery(query, null);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                }
        );

        MenuItem postItemPage = menu.getItem(1);
        postItemPage.setVisible(true);
        postItemPage.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openPostItem();
                return true;
            }
        });

        MenuItem signOutBtn = menu.getItem(2);
        signOutBtn.setVisible(true);
        signOutBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),
                        "Successfully signed out.",
                        Toast.LENGTH_LONG).show();
                goToHomePage();
                return true;
            }
        });

        MenuItem refreshButton = menu.getItem(3);
        refreshButton.setVisible(true);
        refreshButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                refreshFeed();
                return true;
            }
        });

        MenuItem manageAccount = menu.getItem(4);
        manageAccount.setVisible(true);
        manageAccount.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openAccountManagementPage();
                return true;
            }
        });

        MenuItem outgoingBids = menu.getItem(5);
        outgoingBids.setVisible(true);
        outgoingBids.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openOutgoingBidsPage();
                return true;
            }
        });

        MenuItem manageItems = menu.getItem(6);
        manageItems.setVisible(true);
        manageItems.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openItemManagementPage();
                return true;
            }
        });
        return true;

    }

    void openPostItem() {
        Intent intent = new Intent(Feed.this, PostItemActivity.class);
        startActivity(intent);
    }

    void openAccountManagementPage() {
        final Intent intent = new Intent(Feed.this, UserAccountAdmin.class);
        NetworkServiceHandler networkServiceHandler = NetworkServiceHandler.getInstance();
        String uid = networkServiceHandler.getCurrentUsersId();
        mRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> userData = (HashMap) dataSnapshot.getValue();
                User user = new User();
                user.populateFromMap(userData);
                UserAccountAdmin.user = user;
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void goToHomePage() {
        Intent intent = new Intent(Feed.this, LoginActivity.class);
        startActivity(intent);
    }

    void openOutgoingBidsPage() {
        Intent intent = new Intent(Feed.this, OutgoingBids.class);
        startActivity(intent);
    }

    void openItemManagementPage() {
        Intent intent = new Intent(Feed.this, ManageMyItems.class);
        startActivity(intent);
    }
}

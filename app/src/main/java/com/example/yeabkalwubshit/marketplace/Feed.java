package com.example.yeabkalwubshit.marketplace;

import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Feed extends AppCompatActivity {

    private ListView mFeedList;
    private FeedAdapter mAdapter;

    private static HashMap<String, Object> dataOrigin;
    private ArrayList<Item> items;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ActionBar actionBar = getSupportActionBar();
        int actionBarColor = Color.rgb(40,60,250);
        int darkerColor = Color.rgb(10,30,200);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
        getWindow().setStatusBarColor(darkerColor);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        initUI();

        refreshFeed();


    }

    private void initUI() {
        mFeedList = findViewById(R.id.feedItemsList);
    }

    private void fillItems() {
        items = new ArrayList<>();
        System.out.println(dataOrigin);
        for(String key : dataOrigin.keySet()) {
            if(key.equals("next_item_id")) continue;
            HashMap<String, Object> itemData = (HashMap) dataOrigin.get(key);
            System.out.println(itemData);
            Item item = new Item();
            item.populateFromMap(itemData);
            items.add(item);
        }
        mAdapter = new FeedAdapter(this, items);
        mFeedList.setAdapter(mAdapter);
    }

    private void refreshFeed() {
        mRef.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> data = (HashMap) dataSnapshot.getValue();
                dataOrigin = data;
                fillItems();
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
                        System.out.println("Tried searching " + query);
                        SearchRecentSuggestions suggestions =
                                new SearchRecentSuggestions(getApplicationContext(),
                                        SampleRecentSuggestionProvider.AUTHORITY,
                                        SampleRecentSuggestionProvider.MODE);
                        suggestions.saveRecentQuery(query, null);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        System.out.println("Typing " + newText);
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
        return true;

    }

    public class FeedAdapter extends ArrayAdapter<Item> {
        public FeedAdapter(Context context, ArrayList<Item> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            Item item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.feed_entry_cell, parent, false);
            }

            System.out.println("In adapter = " + item.createMap());

            TextView title = convertView.findViewById(R.id.feedItemTitle);
            TextView desc = convertView.findViewById(R.id.feedItemDesc);
            TextView price = convertView.findViewById(R.id.feedItemPrice);
            ImageView image = convertView.findViewById(R.id.feedItemImage);

            title.setText(item.getTitle());
            desc.setText(item.getDescription());
            long priceVal = item.getPriceInCents();
            double priceDisp = priceVal/100.0;
            price.setText("$"+Double.toString(priceDisp));


            if(item.getImageURL() != null && !item.getImageURL().equals("")) {
                StorageReference islandRef = mStorageRef.child(item.getImageURL());

                final long TWO_MEGABYTE = 2 * 1024 * 1024;
                Task<byte[]> taskImage = islandRef.getBytes(TWO_MEGABYTE);
                try {
                    while(!taskImage.isComplete()) {
                        Thread.sleep(100);
                    }
                } catch (Exception e) {}

                image.getLayoutParams().height = 300; // OR
                image.getLayoutParams().width = 300;

                byte[] bytes =  taskImage.getResult();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
            } else {
                image.setMaxHeight(0);
                image.setMaxWidth(0);
            }

            return convertView;
        }
    }

    void openPostItem() {
        Intent intent = new Intent(Feed.this, PostItemActivity.class);
        startActivity(intent);
    }

    void goToHomePage() {
        Intent intent = new Intent(Feed.this, LoginActivity.class);
        startActivity(intent);
    }
}

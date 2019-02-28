package com.example.yeabkalwubshit.marketplace;

import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.provider.SearchRecentSuggestions;
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class Feed extends AppCompatActivity {

    private ListView mFeedList;
    private FeedAdapter mAdapter;

    public static HashMap<String, Object> dataOrigin;
    private ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ActionBar actionBar = getSupportActionBar();
        int actionBarColor = Color.rgb(40,60,250);
        int darkerColor = Color.rgb(10,30,200);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
        getWindow().setStatusBarColor(darkerColor);

        initUI();
        fillItems();

        mAdapter = new FeedAdapter(this, items);
        mFeedList.setAdapter(mAdapter);


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

            title.setText(item.getTitle());
            desc.setText(item.getDescription());
            price.setText(Long.toString(item.getPriceInCents()));

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

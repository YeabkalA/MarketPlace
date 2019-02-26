package com.example.yeabkalwubshit.marketplace;

import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Feed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ActionBar actionBar = getSupportActionBar();
        int actionBarColor = Color.rgb(40,60,250);
        int darkerColor = Color.rgb(10,30,200);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
        getWindow().setStatusBarColor(darkerColor);
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

    void openPostItem() {
        Intent intent = new Intent(Feed.this, PostItemActivity.class);
        startActivity(intent);
    }

    void goToHomePage() {
        Intent intent = new Intent(Feed.this, LoginActivity.class);
        startActivity(intent);
    }
}

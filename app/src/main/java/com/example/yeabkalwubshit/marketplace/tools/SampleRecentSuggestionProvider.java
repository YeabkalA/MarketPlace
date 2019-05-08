package com.example.yeabkalwubshit.marketplace.tools;

import android.content.SearchRecentSuggestionsProvider;

public class SampleRecentSuggestionProvider
        extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY =
            "com.example.yeabkalwubshit.marketplace.tools.SampleRecentSuggestionProvider";

    public static final int MODE = DATABASE_MODE_QUERIES;

    public SampleRecentSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
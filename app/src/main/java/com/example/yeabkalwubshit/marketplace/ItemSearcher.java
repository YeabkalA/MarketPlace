package com.example.yeabkalwubshit.marketplace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ItemSearcher {
    HashMap<String, Item> items;

    public ItemSearcher(ArrayList<Item> itemsList) {
        items = new HashMap<>();

        for(Item item: itemsList) {
            items.put(item.getId(), item);
        }
    }

    public ArrayList<Item> runQuery(String queryString, int maxResult) {
        queryString = queryString.toLowerCase();
        final HashMap<String, Integer> corelationMap = new HashMap<>();
        ArrayList<String> ids = new ArrayList<>();
        for(String itemId: items.keySet()) {
            int corelation = getLongestCommonSubsequenceLength(
                    items.get(itemId).getTitle().toLowerCase(),
                    queryString);
            corelationMap.put(itemId, corelation);
            ids.add(itemId);
        }

        Collections.sort(ids, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return corelationMap.get(o2) - corelationMap.get(o1);
            }
        });
        ArrayList<Item> rv = new ArrayList<>();
        for(int i=0; i<maxResult; i++) {
            rv.add(items.get(ids.get(i)));
        }
        return rv;
    }

    public static int getLongestCommonSubsequenceLength(String s1, String s2) {
        int[][] memo = new int[s1.length()+1][s2.length()+1];
        for(int i=0; i<s1.length(); i++) {
            for(int j=0; j<s2.length(); j++) {
                if(s1.charAt(i) == s2.charAt(j)) {
                    memo[i+1][j+1] = memo[i][j] + 1;
                } else {
                    int left = memo[i][j+1];
                    int up = memo[i+1][j];
                    memo[i+1][j+1] = Math.max(left, up);
                }
            }
        }
        return memo[s1.length()][s2.length()];
    }
}

package com.example.yeabkalwubshit.marketplace;

import com.example.yeabkalwubshit.marketplace.objects.Item;
import com.example.yeabkalwubshit.marketplace.tools.ItemSearcher;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class ItemSearcherTest {

    static final String TEST_ITEM_TITLE1 = "abcefsdllsdd";
    static final String TEST_ITEM_TITLE2 = "kaejdsdldd";

    static ArrayList<Item> items;

    static final String[] TEST_ITEM_TITLES = {
            "abcefsdllsdd",
            "kaejdsdldd",
            "A New Lion",
            "A physics book",
            "A geography book",
            "An appLied PhySicS book"
    };

    @Before
    public void init() {
        items = new ArrayList<>();

        for(int i=0; i < TEST_ITEM_TITLES.length; i++) {
            String id = Integer.toString(i);
            String title = TEST_ITEM_TITLES[i];
            Item item = getItemWithTitle(id, title);
            items.add(item);
        }
    }

    @Test
    public void testSearch() {
        ItemSearcher searcher = new ItemSearcher(items);
        ArrayList<Item> result = searcher.runQuery("phys book", 3);
        assertTrue(result.get(0).getId().equals("3"));
        assertTrue(result.get(1).getId().equals("5"));
        assertTrue(result.get(2).getId().equals("4"));
    }

    @Test
    public void lcsLengthTest() {
        int lcsLength = ItemSearcher.getLongestCommonSubsequenceLength(
                TEST_ITEM_TITLES[0],
                TEST_ITEM_TITLES[1]);
        // Expecting LCS to be 'aesdldd', which has length is 7.
        assertTrue(lcsLength == 7);
    }

    public Item getItemWithTitle(String id, String title) {
        Item item = new Item.Builder()
                .setId(id)
                .setTitle(title)
                .build();
        return item;
    }
}

package com.example.yeabkalwubshit.marketplace;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ItemTest {
    private static String TITLE = "Test Item";
    private static String DESCRIPTION = "Test Item Description";
    private static String ID = "1234";
    private static String OWNER_ID = "abcdedfgh";
    private static String OWNER_ZIP = "12345";
    private static Long PRICE_IN_CENTS = 1234L;
    private static String CONDITION = "NEW";

    private static Item ITEM;

    @Before
    public void initItem() {
        ITEM = new Item.Builder()
                .setDescription(DESCRIPTION)
                .setId(ID)
                .setOwnerId(OWNER_ID)
                .setOwnerZip(OWNER_ZIP)
                .setPriceInCents(PRICE_IN_CENTS)
                .setTitle(TITLE)
                .setCondition(CONDITION)
                .build();
    }

    @Test
    public void successfulItemCreation() {

        assertTrue(ITEM != null);
    }

    @Test
    public void priceConversionToString() {
        Long itemPriceInCents = ITEM.getPriceInCents();
        String dollarRep = Item.getDollarRepresentation(itemPriceInCents);
        assertTrue(dollarRep.equals("12.34"));
    }

}

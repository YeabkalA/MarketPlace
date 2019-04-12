package com.example.yeabkalwubshit.marketplace;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetworkServiceHandler {

    private static NetworkServiceHandler instance;

    private NetworkServiceHandler() {}

    public static NetworkServiceHandler getInstance() {
        // Lazy instantiation.
        if(instance == null) {
            instance = new NetworkServiceHandler();
            instance.auth = FirebaseAuth.getInstance();
            instance.database = FirebaseDatabase.getInstance();
        }
        return instance;
    }

    private FirebaseAuth auth;
    private FirebaseDatabase database;


    public void bidForItem(final String itemId, final Bid bid) {
        final DatabaseReference dbRef = instance.database.getReference();
        final String currentUserId = getCurrentUsersId();
        dbRef.child("items").child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Item item = new Item();
                item.populateFromMap((HashMap) dataSnapshot.getValue());
                List<Bid> bids = item.getBids();
                if(bids.size() != 0) {
                    for(Bid oldBid: bids) {
                        if(oldBid.getIssuerId().equals(currentUserId)) {
                            bid.setId(oldBid.getId());
                            dbRef.child("items").child(itemId).child("bids")
                                    .child(oldBid.getId().toString()).setValue(bid.createMap());
                            dbRef.child("users").child(currentUserId).child("bids").child(oldBid.getId().toString())
                                    .setValue(bid.createMap());
                            return;
                        }
                    }
                }
                dbRef.child("next_bid_id").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String nextBidId = (String) dataSnapshot.getValue();
                        bid.setId(Long.parseLong(nextBidId));
                        System.out.println("The next bid id" + nextBidId);
                        dbRef.child("items").child(itemId).child("bids").child(nextBidId).setValue(
                                bid.createMap());
                        // Add a reference to the bid in user data.
                        dbRef.child("users").child(getCurrentUsersId()).child("bids").child(nextBidId)
                                .setValue(bid.createMap());
                        dbRef.child("next_bid_id").setValue(
                                Integer.toString((Integer.parseInt(nextBidId)) + 1)).addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Failurees "+e.getMessage());
                                    }
                                }
                        );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getCurrentUsersId() {
        return instance.auth.getCurrentUser().getUid();
    }
}

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

import java.util.HashMap;

public class NetworkServiceHandler {
    private static FirebaseAuth auth;
    private static FirebaseStorage storage;
    private static FirebaseDatabase database;

    private static void checkInit() {
        if(auth == null || storage == null || database == null) {
            auth = FirebaseAuth.getInstance();
            storage = FirebaseStorage.getInstance();
            database = FirebaseDatabase.getInstance();
        }
    }

    public static void bidForItem(final String itemId, final Bid bid) {
        checkInit();
        final DatabaseReference dbRef = database.getReference();
        dbRef.child("next_bid_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nextBidId = (String) dataSnapshot.getValue();
                System.out.println("The next bid id" + nextBidId);
                dbRef.child("items").child(itemId).child("bids").child(nextBidId).setValue(
                        bid.createMap()
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failurees"+e.getMessage());
                    }
                });
                dbRef.child("next_bid_id").setValue(
                        Integer.toString((Integer.parseInt(nextBidId)) + 1)).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Failurees"+e.getMessage());
                            }
                        }
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static String getCurrentUsersId() {
        checkInit();
        return auth.getCurrentUser().getUid();
    }
}

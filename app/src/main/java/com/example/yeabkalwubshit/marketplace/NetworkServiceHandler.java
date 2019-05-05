package com.example.yeabkalwubshit.marketplace;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

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
            instance.storage = FirebaseStorage.getInstance();
        }
        return instance;
    }

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;


    public void bidForItem(final Bid bid) {
        final String itemId = bid.getItemId();
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

    public void removeBidFromUserData(Bid bid) {
        DatabaseReference ref = database.getReference();
        ref.child("users").child(bid.getIssuerId()).child("bids").child(bid.getId().toString()).removeValue();
    }

    public void removeItem(Item item) {
        DatabaseReference ref = database.getReference();
        String ownerId = item.getOwnerId();
        ref.child("users").child(ownerId).child("items").child(item.getId()).removeValue();
        ref.child("items").child(item.getId()).removeValue();
    }

    public void finalizeItem(Item item) {
        DatabaseReference ref = database.getReference();
        String bidWinnerId = item.getWinningBid().getIssuerId();
        ref.child("items").child(item.getId()).child("status").child("status")
                .setValue(ItemStatus.STATUS_FINALIZING);
        ref.child("items").child(item.getId()).child("status").child("bidWinnerId")
                .setValue(bidWinnerId);

    }

    public void completeDeal(Item item) {
        DatabaseReference ref = database.getReference();
        String bidWinnerId = item.getWinningBid().getIssuerId();
        ref.child("items").child(item.getId()).child("status").child("status")
                .setValue(ItemStatus.STATUS_COMPLETED);

    }

    public void rate(String userId, final int ratingValue) {
        final DatabaseReference ratingRef = database.getReference()
                .child("users").child(userId).child("rating");
        ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> data = (HashMap) dataSnapshot.getValue();
                Rating rating = new Rating();
                rating.populateFromMap(data);
                rating.update(ratingValue);
                ratingRef.setValue(rating.createMap());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateAccountInfo(UserAccountDetailChangeRequest request) {
        String currentUser = auth.getCurrentUser().getUid();
        DatabaseReference mainRef = database.getReference();
        DatabaseReference userdBRef = mainRef.child("users").child(currentUser);
        userdBRef.child("firstName").setValue(request.getFirstName());
        userdBRef.child("lastName").setValue(request.getLastName());
        userdBRef.child("phoneNumber").setValue(request.getPhoneNumber());
        DatabaseReference addressdBRef = userdBRef.child("address");
        addressdBRef.child("city").setValue(request.getCity());
        addressdBRef.child("zip").setValue(request.getZipCode());
        addressdBRef.child("state").setValue(request.getState());
    }

    public void uploadImage(Activity act, User user, Uri filePath) {
        final Activity activity = act;
        if(filePath != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setView(R.xml.progress);
            final Dialog dialog = builder.create();
            dialog.show();
            dialog.setTitle("Uploading your image...");

            String imageStoragePath = getImagePathString(getCurrentUsersId());
            if(user != null) user.setImageURL(imageStoragePath);

            database.getReference().child("users").child(getCurrentUsersId()).child("imageUrl")
                    .setValue(imageStoragePath);
            storage.getReference().child(imageStoragePath).putFile(filePath)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(activity, "Please try again later",
                            Toast.LENGTH_LONG);
                }
            });
        } else {
            Toast.makeText(activity, "NO ITEM", Toast.LENGTH_LONG);
        }
    }

    private static String getImagePathString(String userId) {
        return "images/users/" + userId;
    }
}

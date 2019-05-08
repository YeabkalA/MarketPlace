package com.example.yeabkalwubshit.marketplace.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.yeabkalwubshit.marketplace.R;
import com.example.yeabkalwubshit.marketplace.activities.ItemDetailView;
import com.example.yeabkalwubshit.marketplace.networkhandlers.NetworkServiceHandler;
import com.example.yeabkalwubshit.marketplace.objects.Bid;
import com.example.yeabkalwubshit.marketplace.objects.Item;
import com.example.yeabkalwubshit.marketplace.objects.Rating;
import com.example.yeabkalwubshit.marketplace.objects.User;
import com.example.yeabkalwubshit.marketplace.networkhandlers.AddressNetworkServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.ViewHolder> {
    private List<Item> items;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView price;
        public TextView winningBid;
        public TextView newUsed;
        public RatingBar rating;
        public TextView distance;
        public ViewPager viewPager;
        public TextView category;
        public TextView posterUserName;

        public View layout;

        private FirebaseStorage mStorage;
        private StorageReference mStorageRef;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            title = v.findViewById(R.id.feedItemTitle);
            price = v.findViewById(R.id.feedItemPrice);
            winningBid = v.findViewById(R.id.feedWinningBid);
            newUsed = v.findViewById(R.id.feedNewUsed);
            distance = v.findViewById(R.id.feedItemDistance);
            rating = v.findViewById(R.id.feedItemRating);
            viewPager = v.findViewById(R.id.feedViewPager);
            category = v.findViewById(R.id.feedItemCategory);
            posterUserName = v.findViewById(R.id.itemPosterUserName);

            mStorage = FirebaseStorage.getInstance();
            mStorageRef = mStorage.getReference();

        }
    }

    public void add(int position, Item item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FeedListAdapter(List<Item> myDataset, Context ctxt) {
        items = myDataset;
        context = ctxt;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FeedListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.feed_entry_cell, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Item item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText(Item.getDollarRepresentation(item.getPriceInCents()));
        holder.price.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        String userId = NetworkServiceHandler.getInstance().getCurrentUsersId();
        if(!userId.equals(item.getOwnerId())) {
            try {

                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("users").child(userId)
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        HashMap<String, Object> userData = (HashMap) dataSnapshot.getValue();
                                        final User user = new User();
                                        user.populateFromMap(userData);
                                        final String userZip = user.getAddress().getZip();

                                        dbRef.child("users").child(item.getOwnerId()).child("address")
                                                .child("zip").addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        final String ownerZip = (String) dataSnapshot.getValue();
                                                        Thread thread = new Thread(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            double d = AddressNetworkServices.getDistanceBetweenTwoZips(
                                                                                    ownerZip, userZip, "mi");
                                                                            holder.distance.setText(Double.toString(d) + " miles away");
                                                                        } catch(Exception e) {
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                        thread.start();

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                                                }
                                        );
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );
            } catch(Exception e) {
            }
        } else {
            holder.distance.setText("");
        }


        if(item.getBids().size()>0) {
            Bid winningBid = item.calculateWinningBid();
            holder.winningBid.setText(Item.getDollarRepresentation(winningBid.getValueInCents()));
        } else {
            holder.winningBid.setText("NA");
        }

        holder.newUsed.setText(item.getCondition());
        FirebaseDatabase.getInstance().getReference().child("users").child(item.getOwnerId()).child("rating")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, Object> data = (HashMap) dataSnapshot.getValue();
                        Rating rating = new Rating();
                        rating.populateFromMap(data);
                        setRating(rating, holder);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        holder.rating.setRating(position%5 + 1);
        holder.category.setText(item.getCategory() == null ? "NO CATEG " : item.getCategory());
        holder.posterUserName.setText(item.getOwnerUserName() == null ? "NOT AVAIL" : item.getOwnerUserName());


        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailView.class);
                ItemDetailView.item = item;
                context.startActivity(intent);
            }
        });

        if(item.getImageUrls() != null
                && item.getImageUrls().size() != 0) {
            ArrayList<String> viewPagerList = new ArrayList<>();

            for(String imageUrl : item.getImageUrls()) {
                StorageReference islandRef = holder.mStorageRef.child(imageUrl);
                Task<Uri> task = islandRef.getDownloadUrl();
                while(!task.isComplete()) {}

                String url = task.getResult().toString();

                viewPagerList.add(url);
            }
            ViewPagerAdapter adapter = new ViewPagerAdapter(this.context, viewPagerList);
            holder.viewPager.setAdapter(adapter);
        }
    }

    void setRating(Rating rating, ViewHolder holder) {
        holder.rating.setRating(rating.getValue());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}
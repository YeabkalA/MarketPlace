package com.example.yeabkalwubshit.marketplace;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Item item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText(Item.getDollarRepresentation(item.getPriceInCents()));
        double distance = -1;
        System.out.println("OZ" + item.getOwnerZip());
        try {
            double d = AddressNetworkServices.getDistanceBetweenTwoZips(item.getOwnerZip(), "75062", "mi");
            distance = d;
            System.out.println("Gotzz" + d);
        } catch(Exception e) {
            System.out.println("Address calc exce:" + e.getMessage());
        }

        holder.distance.setText(Double.toString(distance));

        if(item.getBids().size()>0) {
            Bid winningBid = item.calculateWinningBid();
            holder.winningBid.setText(Item.getDollarRepresentation(winningBid.getValueInCents()));
        } else {
            holder.winningBid.setText("NA");
        }

        holder.newUsed.setText(item.getCondition());
        holder.rating.setRating(position%5 + 1);
        holder.category.setText(item.getCategory() == null ? "NO CATEG " : item.getCategory());


        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailView.class);
                ItemDetailView.item = item;
                context.startActivity(intent);
            }
        });

        if(item.getImageURL() != null
                && !item.getImageURL().equals("")) {
            StorageReference islandRef = holder.mStorageRef.child(item.getImageURL());
            Task<Uri> task = islandRef.getDownloadUrl();
            while(!task.isComplete()) {}

            String url = task.getResult().toString();

            ArrayList<String> viewPagerList = new ArrayList<>();
            viewPagerList.add(url);
            viewPagerList.add("https://d3nfwcxd527z59.cloudfront.net/content/uploads/2019/03/21104030/Francis-Coquelin-Valencia.jpg");

            ViewPagerAdapter adapter = new ViewPagerAdapter(this.context, viewPagerList);
            holder.viewPager.setAdapter(adapter);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}
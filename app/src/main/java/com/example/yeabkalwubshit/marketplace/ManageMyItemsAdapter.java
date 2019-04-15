package com.example.yeabkalwubshit.marketplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManageMyItemsAdapter extends RecyclerView.Adapter<ManageMyItemsAdapter.ViewHolder> {
    private List<Item> items;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView numBids;
        public TextView winningBid;
        public Button finalize;
        public Button delete;

        public NetworkServiceHandler networkServiceHandler;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            title = v.findViewById(R.id.manageItemsTitle);
            numBids = v.findViewById(R.id.manageItemsNumBids);
            winningBid = v.findViewById(R.id.manageItemsWinningBid);
            finalize = v.findViewById(R.id.manageItemsFinalize);
            delete = v.findViewById(R.id.manageItemsDelete);
            networkServiceHandler = NetworkServiceHandler.getInstance();
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

    public ManageMyItemsAdapter(List<Item> myDataset, Context ctxt) {
        items = myDataset;
        context = ctxt;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ManageMyItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.manage_my_items_cell, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Item item = items.get(position);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        holder.title.setText(item.getTitle());
        holder.numBids.setText(Integer.toString(item.getBids().size()));
        Bid winningBid = item.calculateWinningBid();
        if(winningBid == null) {
            holder.winningBid.setText("NA");
        } else {
            String winningBidDescription = Item.getDollarRepresentation(winningBid.getValueInCents());
            holder.winningBid.setText(winningBidDescription);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Bid> bids = new ArrayList<>(item.getBids());
                for(Bid bid: bids) {
                    holder.networkServiceHandler.removeBidFromUserData(bid);
                }
                holder.networkServiceHandler.removeItem(item);
                remove(position);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}
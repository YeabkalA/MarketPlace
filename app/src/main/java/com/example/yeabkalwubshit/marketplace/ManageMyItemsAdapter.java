package com.example.yeabkalwubshit.marketplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        holder.numBids.setText(Integer.toString(item.getBids().size()) + " bids");
        final Bid winningBid = item.calculateWinningBid();
        if(winningBid == null) {
            holder.winningBid.setText("No bid yet.");
            holder.finalize.setVisibility(View.GONE);
        } else {
            String winningBidDescription = Item.getDollarRepresentation(winningBid.getValueInCents());
            holder.winningBid.setText("Winning: $" + winningBidDescription);
            switch(item.getStatus().getStatus()) {
                case ItemStatus.STATUS_AVAILABLE: {
                    holder.finalize.setText("FINALIZE");

                    holder.finalize.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(winningBid == null) {
                                Toast.makeText(context, "No bid yet for this item.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                        .setTitle("Are you sure you want to finalize this item?");
                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NetworkServiceHandler networkServiceHandler =
                                                NetworkServiceHandler.getInstance();
                                        networkServiceHandler.finalizeItem(item);
                                        holder.finalize.setText("CONTACT BUYER");
                                        holder.delete.setVisibility(View.GONE);
                                    }
                                });
                                builder.show();
                            }
                        }
                    });
                    break;
                }
                case ItemStatus.STATUS_FINALIZING: {
                    holder.finalize.setText("CONTACT BUYER");
                    holder.delete.setVisibility(View.GONE);
                    break;
                }
                case ItemStatus.STATUS_COMPLETED: {
                    holder.finalize.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);
                    break;
                }
            }

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailView.class);
                ItemDetailView.item = item;
                context.startActivity(intent);
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}
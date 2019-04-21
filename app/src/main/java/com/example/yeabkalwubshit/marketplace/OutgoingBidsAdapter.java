package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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

public class OutgoingBidsAdapter extends RecyclerView.Adapter<OutgoingBidsAdapter.ViewHolder> {
    private List<Bid> bids;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView myBid;
        public TextView status;
        public TextView winningBid;
        public CardView cardView;
        public Button contactOwner;
        public Button finalize;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            title = v.findViewById(R.id.outgoingBidItemTitle);
            myBid = v.findViewById(R.id.outgoingMyBid);
            status = v.findViewById(R.id.outgoingStatus);
            winningBid = v.findViewById(R.id.outgoingWinningBid);
            cardView = v.findViewById(R.id.outgoingBidCardView);
            contactOwner = v.findViewById(R.id.outgoingContact);
            finalize = v.findViewById(R.id.outgoingFinalize);
        }
    }

    public void add(int position, Bid bid) {
        bids.add(position, bid);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        bids.remove(position);
        notifyItemRemoved(position);
    }

    public OutgoingBidsAdapter(List<Bid> myDataset, Context ctxt) {
        bids = myDataset;
        context = ctxt;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OutgoingBidsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.outgoing_bid_cell, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Bid bid = bids.get(position);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference();

        holder.contactOwner.setVisibility(View.GONE);
        holder.finalize.setVisibility(View.GONE);

        mRef.child("items").child(bid.getItemId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Item item = new Item();
                item.populateFromMap((HashMap) dataSnapshot.getValue());
                Bid winningBid = item.calculateWinningBid();
                boolean betterBid = bid.getValueInCents() >= winningBid.getValueInCents();
                holder.title.setText(item.getTitle());
                holder.myBid.setText("My bid: " + Item.getDollarRepresentation(bid.getValueInCents()));
                NetworkServiceHandler networkServiceHandler = NetworkServiceHandler.getInstance();
                String userId = networkServiceHandler.getCurrentUsersId();

                int green = Color.rgb(50, 200, 50);
                int red = Color.rgb(200, 50, 50);
                int blue = Color.rgb(50, 150, 200);
                int lightYellow = Color.rgb(255, 255, 153);
                int lightRed = Color.rgb(255, 230, 230);
                final int lightGreen = Color.rgb(217, 255, 179);

                holder.winningBid.setText("Winning: " + Item.getDollarRepresentation(winningBid.getValueInCents()));

                if(item.getStatus() == null || item.getStatus().getStatus()
                        .equals(ItemStatus.STATUS_AVAILABLE)) {
                    holder.status.setBackgroundColor(betterBid ? blue : red);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ItemDetailView.class);
                            ItemDetailView.item = item;
                            context.startActivity(intent);
                        }
                    });
                } else if(item.getStatus().getStatus().equals(ItemStatus.STATUS_FINALIZING)) {
                    if(betterBid && userId.equals(item.getStatus().getBidWinnerId())) {
                        //holder.itemView.setBackgroundColor(Color.YELLOW);
                        holder.cardView.setBackgroundColor(lightYellow);
                        holder.finalize.setVisibility(View.VISIBLE);
                        holder.contactOwner.setVisibility(View.VISIBLE);

                        holder.finalize.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                        .setTitle("Are you sure you want to finalize this item?");
                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NetworkServiceHandler networkServiceHandler =
                                                NetworkServiceHandler.getInstance();
                                        networkServiceHandler.completeDeal(item);
                                        holder.finalize.setVisibility(View.GONE);
                                        holder.contactOwner.setVisibility(View.GONE);
                                        holder.cardView.setBackgroundColor(lightGreen);
                                    }
                                });
                                builder.show();
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
                    } else {
                        holder.cardView.setBackgroundColor(lightRed);
                    }
                } else if(item.getStatus().getStatus().equals(ItemStatus.STATUS_COMPLETED)) {
                    if(userId.equals(item.getStatus().getBidWinnerId())) {
                        holder.cardView.setBackgroundColor(lightGreen);
                    } else {
                        holder.cardView.setBackgroundColor(lightRed);
                        holder.winningBid.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return bids.size();
    }

}
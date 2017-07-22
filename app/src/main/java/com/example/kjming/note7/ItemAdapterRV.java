package com.example.kjming.note7;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kjming on 5/30/2017.
 */

public class ItemAdapterRV extends RecyclerView.Adapter<ItemAdapterRV.ViewHolder> {
    private List<Item> items;
    public ItemAdapterRV(List<Item> items) {
        this.items = items;
    }
    @Override
    public ItemAdapterRV.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemAdapterRV.ViewHolder holder, int position) {
        final Item item = items.get(position);
        GradientDrawable background = (GradientDrawable)holder.typeColor.getBackground();
        background.setColor(item.getColor().parseColor());

        holder.titleView.setText(item.getTitle());
        holder.dateView.setText(item.getLocaleDatetime());

        holder.selectedItem.setVisibility(item.isSelected()?View.VISIBLE:View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(Item item) {
        items.add(item);
        notifyItemChanged(items.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout typeColor;
        protected ImageView selectedItem;
        protected TextView titleView;
        protected TextView dateView;
        protected View rootView;
        public ViewHolder(View view) {
            super(view);
            typeColor = (RelativeLayout)itemView.findViewById(R.id.typeColor);
            selectedItem =(ImageView)itemView.findViewById(R.id.selectedItem);
            titleView = (TextView)itemView.findViewById(R.id.titleText);
            dateView =(TextView) itemView.findViewById(R.id.dateText);
            rootView = view;

        }
    }

}

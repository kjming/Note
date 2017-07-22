package com.example.kjming.note7;

/**
 * Created by Kjming on 4/20/2017.
 */
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;


public class ItemAdapter extends ArrayAdapter<Item> {

    private int mResource;
    private  List<Item> mitems;

    public ItemAdapter(Context context,int resource,List<Item> items) {
        super (context,resource,items);
        mResource = resource;
        mitems = items;
    }


    @NonNull
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        LinearLayout itemView;
        final  Item item = getItem(position);
        if (convertView == null) {
            itemView = new LinearLayout(getContext());
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(mResource,itemView,true);
        }else {
            itemView = (LinearLayout) convertView;
        }

        RelativeLayout typeColor = (RelativeLayout)itemView.findViewById(R.id.typeColor);
        ImageView selectedItem = (ImageView) itemView.findViewById(R.id.selectedItem);
        TextView titleView = (TextView) itemView.findViewById(R.id.titleText);
        TextView dateView = (TextView) itemView.findViewById(R.id.dateText);

        GradientDrawable background = (GradientDrawable)typeColor.getBackground();
        if (item == null) {
            background.setColor(Color.DKGRAY);
            titleView.setText("");
            dateView.setText("");
            selectedItem.setVisibility(View.INVISIBLE);
        } else {
            if (item.getColor() == null) {
                background.setColor(Color.DKGRAY);
            } else {
                background.setColor(item.getColor().parseColor());
            }
            titleView.setText(item.getTitle());
            dateView.setText(item.getLocaleDatetime());

            selectedItem.setVisibility(item.isSelected() ? View.VISIBLE : View.INVISIBLE);
        }
        return itemView;
    }

    public void set(int index ,Item item) {
        if(index>=0&&index<mitems.size()) {
            mitems.set(index,item);
            notifyDataSetChanged();
        }
    }

    public Item get(int index) {
        return  mitems.get(index);
    }



}

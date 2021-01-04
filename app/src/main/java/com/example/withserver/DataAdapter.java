package com.example.withserver;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.withserver.data.Item;

import java.util.ArrayList;
import java.util.Comparator;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ItemViewHolder> {

    interface ItemClick {
        void itemClickListener(Item item) ;
    }

    private ItemClick itemClick ;
    private ArrayList<Item> data ;
    public void setData(ArrayList<Item> data) {
        this.data = data ;
        notifyDataSetChanged() ;
    }

    public DataAdapter(ItemClick itemClick) {
        this.itemClick = itemClick ;
    }

    public void sortData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.data.sort(new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    int o1View = Integer.parseInt(o1.readcount) ;
                    int o2View = Integer.parseInt(o2.readcount) ;


                    if(o1View == o2View) return 0 ;
                    else if (o1View > o2View) return -1 ;
                    else return 1 ;
                }
            });
        }
        notifyDataSetChanged() ;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false) ;
        ItemViewHolder holder = new ItemViewHolder(v) ;
        return holder ;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = data.get(position);
        holder.place.setText(item.addr);
        holder.title.setText(item.title);
        holder.readCount.setText("views " + item.readcount) ;
        if (item.mainimage != null)
            Glide.with(holder.itemView).load(item.mainimage).into(holder.imageView);
        else {
            holder.imageView.setImageResource(R.drawable.ic_image_not_supported_24px);
        }

        holder.itemView.setOnClickListener(v->{
            this.itemClick.itemClickListener(item) ;
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size() ;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        View itemView ;
        ImageView imageView ;
        TextView title ;
        TextView place ;
        TextView readCount ;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView) ;
            this.itemView = itemView ;
            imageView = itemView.findViewById(R.id.dataImage) ;
            title = itemView.findViewById(R.id.titleText) ;
            place = itemView.findViewById(R.id.placeText) ;
            readCount = itemView.findViewById(R.id.readCount) ;
        }
    }
}

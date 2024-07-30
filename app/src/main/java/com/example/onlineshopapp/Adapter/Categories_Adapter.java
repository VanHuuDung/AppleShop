package com.example.onlineshopapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlineshopapp.Activity.ProductTypeActivity;
import com.example.onlineshopapp.Model.Categories;
import com.example.onlineshopapp.R;

import java.util.List;

public class Categories_Adapter extends RecyclerView.Adapter<Categories_Adapter.MyViewHolder> {
    private List<Categories> cate_List;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView categories_name;
        public ImageView catrgories_img;
        LinearLayout linearLayout;
        public MyViewHolder(View view){
            super(view);
            categories_name = view.findViewById(R.id.categories_name);
            catrgories_img = view.findViewById(R.id.categories_img);
            linearLayout = view.findViewById(R.id.layout_categories);
        }
    }

    public Categories_Adapter(List<Categories> cate_List, Context context) {
        this.cate_List = cate_List;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categories, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Categories_Adapter.MyViewHolder holder, int position) {
        Categories categories = cate_List.get(position);
        holder.categories_name.setText(categories.getName());
        //   Picasso.get().load(categories.getImg()).into(holder.catrgories_img);
        Glide.with(holder.itemView.getContext()).load(cate_List.get(position).getImage_url()).into(holder.catrgories_img);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductTypeActivity.class);
                intent.putExtra("categories", cate_List.get(holder.getAdapterPosition()).getId());
                intent.putExtra("url_image", cate_List.get(holder.getAdapterPosition()).getImage_url());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cate_List.size();
    }
}

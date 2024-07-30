package com.example.onlineshopapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlineshopapp.Activity.Product_detailsActivity;
import com.example.onlineshopapp.Model.Products;
import com.example.onlineshopapp.R;

import java.util.List;

public class Products_Adapter extends RecyclerView.Adapter<Products_Adapter.MyViewHolder>{
    private List<Products> pro_List;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView pro_name, pro_price, pro_rating;
        RatingBar ratingBar;
        public ImageView pro_img, star_img;
        LinearLayout linearLayout;
        public MyViewHolder(@NonNull View view){
            super(view);
            pro_name = view.findViewById(R.id.txt_name_product);
            pro_price = view.findViewById(R.id.txt_price_product);
            pro_rating = view.findViewById(R.id.txt_rating);
            ratingBar = view.findViewById(R.id.ratingBar_product);
            pro_img = view.findViewById(R.id.img_product);
            star_img = view.findViewById(R.id.img_star);
            linearLayout = view.findViewById(R.id.layout_product_details);
        }
    }

    public Products_Adapter(List<Products> pro_List, Context context) {
        this.pro_List = pro_List;
        this.context = context;
    }
    @NonNull
    @Override
    public Products_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_products, parent, false);
        return new Products_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Products_Adapter.MyViewHolder holder, int position) {
        Products products = pro_List.get(position);
        holder.pro_name.setText(products.getName());
        holder.pro_price.setText(convertToVND(String.format("%dđ", products.getPrice())));
        holder.pro_rating.setText(String.format("%.1f", products.getRating()));
        holder.ratingBar.setRating((float) products.getRating());
        Glide.with(holder.itemView.getContext()).load(pro_List.get(position).getImage_url()).into(holder.pro_img);
        holder.star_img.setImageResource(R.drawable.danhgia);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Product_detailsActivity.class);
                intent.putExtra("active", pro_List.get(holder.getAdapterPosition()).getActive());
                intent.putExtra("category_id", pro_List.get(holder.getAdapterPosition()).getCategory_id());
                intent.putExtra("created_at", pro_List.get(holder.getAdapterPosition()).getCreated_at());
                intent.putExtra("description", pro_List.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("image_url", pro_List.get(holder.getAdapterPosition()).getImage_url());
                intent.putExtra("featured", pro_List.get(holder.getAdapterPosition()).getFeatured());
                intent.putExtra("name", pro_List.get(holder.getAdapterPosition()).getName());
                intent.putExtra("price", pro_List.get(holder.getAdapterPosition()).getPrice());
                intent.putExtra("quantity", pro_List.get(holder.getAdapterPosition()).getQuantity());
                intent.putExtra("rating", pro_List.get(holder.getAdapterPosition()).getRating());
                intent.putExtra("id", pro_List.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pro_List.size();
    }

    public String convertToVND(String priceString) {
        String cleanPriceString = priceString.replaceAll("[^\\d]", "");
        long priceLong = Long.parseLong(cleanPriceString);
        String vndString = String.format("%,d", priceLong) + " VND";

        return vndString;
    }
}

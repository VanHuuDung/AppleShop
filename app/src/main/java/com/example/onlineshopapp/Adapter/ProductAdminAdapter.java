package com.example.onlineshopapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.onlineshopapp.Activity.AdminProductDetailActivity;
import com.example.onlineshopapp.Model.Products;
import com.example.onlineshopapp.R;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ProductAdminAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<Products> dataList;
    public ProductAdminAdapter(Context context, List<Products> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_admin_item, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getImage_url()).into(holder.recImage);
        holder.recName.setText(dataList.get(position).getName());
        holder.recQuantity.setText(String.valueOf(dataList.get(position).getQuantity()));
        holder.recPrice.setText(String.valueOf("Giá: " + dataList.get(position).getPrice()));
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdminProductDetailActivity.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getImage_url());
                intent.putExtra("Name", dataList.get(holder.getAdapterPosition()).getName());
                intent.putExtra("Quantity", dataList.get(holder.getAdapterPosition()).getQuantity());
                intent.putExtra("Price", dataList.get(holder.getAdapterPosition()).getPrice());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Id",dataList.get(holder.getAdapterPosition()).getId());
                intent.putExtra("CatId",dataList.get(holder.getAdapterPosition()).getCategory_id());
                intent.putExtra("CreatedAt",dataList.get(holder.getAdapterPosition()).getCreated_at());
                intent.putExtra("IsFeatured",dataList.get(holder.getAdapterPosition()).getFeatured());
                intent.putExtra("Rating",dataList.get(holder.getAdapterPosition()).getRating());
                intent.putExtra("Specifications", dataList.get(holder.getAdapterPosition()).getSpecifications());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public void searchDataList(ArrayList<Products> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}
class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView recImage;
    TextView recName, recPrice, recQuantity;
    CardView recCard;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recName = itemView.findViewById(R.id.recName);
        recPrice = itemView.findViewById(R.id.recPrice);
        recQuantity = itemView.findViewById(R.id.recQuantity);
    }
}

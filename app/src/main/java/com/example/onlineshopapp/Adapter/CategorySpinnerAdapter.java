package com.example.onlineshopapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.example.onlineshopapp.Model.Categories;
import com.example.onlineshopapp.R;

import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<Categories> {

    private Context context;
    private List<Categories> categoriesList;

    public CategorySpinnerAdapter(Context context, List<Categories> categoriesList) {
        super(context, R.layout.category_spinner_item, categoriesList);
        this.context = context;
        this.categoriesList = categoriesList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.category_spinner_item, parent, false);
        }

        Categories category = categoriesList.get(position);

        ImageView imgCategory = view.findViewById(R.id.imgCategory);
        TextView txtCategoryName = view.findViewById(R.id.txtCategoryName);

        txtCategoryName.setText(category.getName());
        Glide.with(context).load(category.getImage_url()).into(imgCategory);

        return view;
    }
}

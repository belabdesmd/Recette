package com.belfoapps.recette.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.belfoapps.recette.R;
import com.belfoapps.recette.base.HomeListener;
import com.belfoapps.recette.models.pojo.Category;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    /*************************************** Declarations *****************************************/
    private List<Category> mCategories;
    private Context context;
    private HomeListener listener;

    /*************************************** Constructor ******************************************/
    public CategoriesAdapter(List<Category> mCategories, HomeListener listener, Context context) {
        this.mCategories = mCategories;
        this.listener = listener;
        this.context = context;
    }

    /*************************************** Methods **********************************************/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_recyclerview_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryTitle.setText(mCategories.get(position).getCategoryTitle());
        Glide.with(context)
                .load(mCategories.get(position).getCategoryCover())
                .error(R.drawable.error_image)
                .fitCenter()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.cover);

        holder.container.setOnClickListener(v -> listener.recipesFromCategory(mCategories.get(position).getId(),
                mCategories.get(position).getCategoryTitle()));
    }

    @Override
    public int getItemCount() {
        if (mCategories == null) return 0;
        else return mCategories.size();
    }

    public void clearAll() {
        if (mCategories != null) mCategories.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Category> categories) {
        mCategories = categories;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView container;
        ImageView cover;
        TextView categoryTitle;

        ViewHolder(View v) {
            super(v);
            container = v.findViewById(R.id.category_container);
            cover = v.findViewById(R.id.category_cover);
            categoryTitle = v.findViewById(R.id.category_title);
        }
    }
}

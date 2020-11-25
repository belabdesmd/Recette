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
import com.belfoapps.recette.base.MainListener;
import com.belfoapps.recette.models.pojo.Recipe;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    /*************************************** Declarations *****************************************/
    private List<Recipe> mRecipes;
    private Context context;
    private HomeListener listener1;
    private MainListener listener2;

    /*************************************** Constructor ******************************************/
    public RecipesAdapter(List<Recipe> mRecipes, HomeListener listener, Context context) {
        this.mRecipes = mRecipes;
        this.context = context;
        this.listener1 = listener;
    }

    public RecipesAdapter(List<Recipe> mRecipes, MainListener listener, Context context) {
        this.mRecipes = mRecipes;
        this.context = context;
        this.listener2 = listener;
    }

    /*************************************** Methods **********************************************/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_recyclerview_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(mRecipes.get(position).getRecipeTitle());
        holder.category.setText(mRecipes.get(position).getCategoryTitle());

        Glide.with(context)
                .load(mRecipes.get(position).getRecipeCover())
                .fitCenter()
                .error(R.drawable.error_image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //holder.loading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //holder.loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.cover);

        if (listener1 != null)
            holder.container.setOnClickListener(v -> listener1.goToRecipe(mRecipes.get(position).getRecipeId()));
        else holder.container.setOnClickListener(v -> listener2.goToRecipe(mRecipes.get(position).getRecipeId(), false));


    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        else return mRecipes.size();
    }

    public void clearAll() {
        if (mRecipes != null) mRecipes.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView container;
        ImageView cover;
        TextView title;
        TextView category;

        ViewHolder(View v) {
            super(v);
            container = v.findViewById(R.id.recipe_container);
            cover = v.findViewById(R.id.recipe_cover);
            title = v.findViewById(R.id.recipe_title);
            category = v.findViewById(R.id.recipe_category);
        }
    }
}

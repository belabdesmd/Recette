package com.belfoapps.recette.ui.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.belfoapps.recette.R;
import com.belfoapps.recette.models.pojo.Shopping;
import com.belfoapps.recette.viewmodels.RecipeViewModel;
import com.belfoapps.recette.viewmodels.ShoppingsViewModel;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
    /*************************************** Declarations *****************************************/
    private ShoppingsViewModel shoppingViewModel = null;
    private RecipeViewModel recipeViewModel = null;
    private Context context;
    private List<Shopping> mShoppings;

    /*************************************** Constructor ******************************************/
    public ShoppingListAdapter(ShoppingsViewModel mPresenter, List<Shopping> mShoppings, Context context) {
        this.shoppingViewModel = mPresenter;
        this.mShoppings = mShoppings;
        this.context = context;
    }

    public ShoppingListAdapter(RecipeViewModel mPresenter, ArrayList<Shopping> mShoppings, Context context) {
        this.recipeViewModel = mPresenter;
        this.mShoppings = mShoppings;
        this.context = context;
    }

    /*************************************** Methods **********************************************/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_recyclerview_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.shopping_text.setText(mShoppings.get(position).getShopping());

        if (mShoppings.get(position).isChecked()) {
            holder.shopping.setChecked(true);
        } else {
            holder.shopping.setChecked(false);
        }

        holder.shopping.setOnCheckedChangeListener((checkBox, isChecked) -> {

            if (isChecked) {
                if (recipeViewModel != null)
                    recipeViewModel.addShopping(mShoppings.get(position));
                else {
                    shoppingViewModel.removeShopping(mShoppings.get(position));
                    holder.shopping_text.setPaintFlags(holder.shopping_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            } else {
                if (recipeViewModel != null)
                    recipeViewModel.removeShopping(mShoppings.get(position));
                else {
                    shoppingViewModel.addShopping(mShoppings.get(position));
                    holder.shopping_text.setPaintFlags(holder.shopping_text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mShoppings == null) return 0;
        else return mShoppings.size();
    }

    public void clearAll() {
        if (mShoppings != null) mShoppings.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Shopping> shoppings) {
        mShoppings = shoppings;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CustomCheckBox shopping;
        TextView shopping_text;

        ViewHolder(View v) {
            super(v);
            shopping = v.findViewById(R.id.shopping);
            shopping_text = v.findViewById(R.id.shopping_text);
        }
    }
}

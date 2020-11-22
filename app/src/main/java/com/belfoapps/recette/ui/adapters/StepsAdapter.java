package com.belfoapps.recette.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.belfoapps.recette.R;

import java.util.ArrayList;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
    /*************************************** Declarations *****************************************/
    private ArrayList<String> mSteps;

    /*************************************** Constructor ******************************************/
    public StepsAdapter(ArrayList<String> mSteps) {
        this.mSteps = mSteps;
    }

    /*************************************** Methods **********************************************/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_recyclerview_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.step.setText(mSteps.get(position));
    }

    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        else return mSteps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView step;

        ViewHolder(View v) {
            super(v);
            step = v.findViewById(R.id.step);
        }
    }
}

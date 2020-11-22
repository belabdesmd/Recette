package com.belfoapps.recette.views.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.belfoapps.recette.R;
import com.belfoapps.recette.base.HomeListener;
import com.belfoapps.recette.base.MainListener;
import com.belfoapps.recette.databinding.HomeFragmentBinding;
import com.belfoapps.recette.databinding.SavedFragmentBinding;
import com.belfoapps.recette.models.pojo.Recipe;
import com.belfoapps.recette.ui.adapters.RecipesAdapter;
import com.belfoapps.recette.ui.custom.RecipesItemDecoration;
import com.belfoapps.recette.viewmodels.SavedViewModel;
import com.belfoapps.recette.views.MainActivity;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SavedFragment extends Fragment {
    private static final String TAG = "SavedFragment";
    private static final int COL_NUM = 2;

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private SavedFragmentBinding mBinding;
    private SavedViewModel mViewModel;
    private RecipesAdapter mAdapter;
    private HomeListener listener;

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (HomeListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = SavedFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set ViewModel
        mViewModel = new ViewModelProvider(this).get(SavedViewModel.class);

        //Init UI
        mBinding.shimmerViewContainer.setVisibility(View.GONE);

        //Get Data
        mViewModel.getRecipesData().observe(getViewLifecycleOwner(), recipes -> {
            if (mAdapter == null)
                initRecyclerView(recipes);
            else updateRecycleView(recipes);
        });
        mViewModel.getRecipes();
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void initRecyclerView(List<Recipe> recipes) {
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(COL_NUM, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new RecipesAdapter(recipes, listener, getContext());

        mBinding.recipesRecyclerview.setLayoutManager(mLayoutManager);
        mBinding.recipesRecyclerview.addItemDecoration(new RecipesItemDecoration());
        mBinding.recipesRecyclerview.setAdapter(mAdapter);

        if (recipes != null && !recipes.isEmpty())
            showRecipesList();
        else showNoSavedRecipes();
    }

    public void updateRecycleView(List<Recipe> recipes) {
        if (mAdapter != null) {
            mAdapter.clearAll();
            mAdapter.addAll(recipes);
        }

        if (!recipes.isEmpty())
            showRecipesList();
        else showNoSavedRecipes();
    }

    public void showNoSavedRecipes() {
        mBinding.errorImage.setImageResource(R.drawable.empty);
        mBinding.errorText.setText(getResources().getString(R.string.empty_error));
        mBinding.error.setVisibility(View.VISIBLE);
        mBinding.recipesRecyclerview.setVisibility(View.GONE);
    }

    public void showRecipesList() {
        mBinding.error.setVisibility(View.GONE);
        mBinding.recipesRecyclerview.setVisibility(View.VISIBLE);
    }
}
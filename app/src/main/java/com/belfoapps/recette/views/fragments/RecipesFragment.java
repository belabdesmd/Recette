package com.belfoapps.recette.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.belfoapps.recette.R;
import com.belfoapps.recette.base.MainListener;
import com.belfoapps.recette.databinding.RecipesFragmentBinding;
import com.belfoapps.recette.models.pojo.Recipe;
import com.belfoapps.recette.ui.adapters.RecipesAdapter;
import com.belfoapps.recette.ui.custom.RecipesItemDecoration;
import com.belfoapps.recette.viewmodels.RecipesViewModel;
import com.belfoapps.recette.views.MainActivity;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipesFragment extends Fragment {
    private static final String TAG = "RecipesFragment";
    private static final int COL_NUM = 2;

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private RecipesFragmentBinding mBinding;
    private RecipesViewModel mViewModel;
    private RecipesAdapter mAdapter;
    private MainListener listener;

    //Observers
    private final Observer<List<Recipe>> recipesObserver = recipes -> {
        if (mAdapter == null)
            initRecyclerView(recipes);
        else updateRecyclerView(recipes);
    };

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = RecipesFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(RecipesViewModel.class);

        //Init UI
        init();

        //Get Data
        if (getArguments() != null) {
            mViewModel.getRecipesData().observe(getViewLifecycleOwner(), recipesObserver);
            mViewModel.getRecipes(getArguments().getLong("categoryId", 0));
            mBinding.categoryName.setText(getArguments().getString("categoryName"));

            mBinding.swipeRefreshRecipes.setOnRefreshListener(() ->
                    mViewModel.getRecipes(getArguments().getLong("categoryId", 0)));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getRecipesData().removeObserver(recipesObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    private void init() {
        mBinding.shimmerViewContainer.startShimmer();

        //Init Listener
        mBinding.back.setOnClickListener(v -> listener.goBack());
    }

    public void initRecyclerView(List<Recipe> recipes) {
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(COL_NUM, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new RecipesAdapter(recipes, listener, getContext());
        mBinding.recipesRecyclerview.setLayoutManager(mLayoutManager);
        mBinding.recipesRecyclerview.addItemDecoration(new RecipesItemDecoration());
        mBinding.recipesRecyclerview.setAdapter(mAdapter);

        if (!recipes.isEmpty())
            showRecipesList();
        else showError();
    }

    public void updateRecyclerView(List<Recipe> recipes) {
        if (mAdapter != null) {
            //Deleting the List of the Categories
            mAdapter.clearAll();

            // Adding The New List of Categories
            mAdapter.addAll(recipes);
        }

        if (!recipes.isEmpty())
            showRecipesList();
        else showError();
    }


    public void showError() {
        mBinding.shimmerViewContainer.stopShimmer();
        mBinding.shimmerViewContainer.setVisibility(View.GONE);

        mBinding.errorImage.setImageResource(R.drawable.error);
        mBinding.errorText.setText(getResources().getString(R.string.general_error));
        mBinding.error.setVisibility(View.VISIBLE);
        mBinding.recipesRecyclerview.setVisibility(View.GONE);

        mBinding.swipeRefreshRecipes.setRefreshing(false);
    }

    public void showRecipesList() {
        mBinding.shimmerViewContainer.stopShimmer();
        mBinding.shimmerViewContainer.setVisibility(View.GONE);

        mBinding.error.setVisibility(View.GONE);
        mBinding.recipesRecyclerview.setVisibility(View.VISIBLE);

        mBinding.swipeRefreshRecipes.setRefreshing(false);
    }

}
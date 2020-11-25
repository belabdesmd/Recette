package com.belfoapps.recette.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
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
    public static final String ID = "id";
    public static final String NAME = "name";
    private static final int COL_NUM = 2;

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private RecipesFragmentBinding mBinding;
    private RecipesViewModel mViewModel;
    private RecipesAdapter mAdapter;
    private MainListener listener;
    private String categoryName;
    private Long categoryId;

    //Callbacks
    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            listener.backHome();
        }
    };

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryName = getArguments().getString("categoryName");
            categoryId = getArguments().getLong("categoryId", 0);
        }
        //Going back
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

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

        //Loading UI
        mBinding.shimmerViewContainer.setVisibility(View.VISIBLE);
        mBinding.shimmerViewContainer.startShimmer();

        //Data Observer
        mViewModel.getRecipesData().observe(getViewLifecycleOwner(), recipesObserver);

        if (savedInstanceState == null) {
            //Load Recipes
            mViewModel.loadRecipes(categoryId);
        } else {
            categoryId = savedInstanceState.getLong(ID);
            categoryName = savedInstanceState.getString(NAME);

            //Init RecyclerView
            initRecyclerView(mViewModel.getRecipes());
        }

        //Load Ads
        mViewModel.loadAd(mBinding.ad);

        //Category Name
        mBinding.categoryName.setText(categoryName);

        //Init Listener
        mBinding.back.setOnClickListener(v -> listener.backHome());
        mBinding.swipeRefreshRecipes.setOnRefreshListener(() ->
                mViewModel.loadRecipes(categoryId));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ID, categoryId);
        outState.putString(NAME, categoryName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.getRecipesData().removeObserver(recipesObserver);
        mBinding = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
        else showError();
    }

    public void updateRecyclerView(List<Recipe> recipes) {
        if (mAdapter != null) {
            //Deleting the List of the Categories
            mAdapter.clearAll();

            // Adding The New List of Categories
            mAdapter.addAll(recipes);
        }

        if (recipes != null && !recipes.isEmpty())
            showRecipesList();
        else showError();
    }


    public void showError() {
        mBinding.shimmerViewContainer.stopShimmer();
        mBinding.shimmerViewContainer.setVisibility(View.GONE);

        mBinding.errorImage.setImageResource(R.drawable.error);
        mBinding.errorText.setText(getResources().getString(R.string.general_error));
        mBinding.error.setVisibility(View.VISIBLE);
        mBinding.recipesContent.setVisibility(View.GONE);

        mBinding.swipeRefreshRecipes.setRefreshing(false);
    }

    public void showRecipesList() {
        mBinding.shimmerViewContainer.stopShimmer();
        mBinding.shimmerViewContainer.setVisibility(View.GONE);

        mBinding.error.setVisibility(View.GONE);
        mBinding.recipesContent.setVisibility(View.VISIBLE);

        mBinding.swipeRefreshRecipes.setRefreshing(false);
    }

}
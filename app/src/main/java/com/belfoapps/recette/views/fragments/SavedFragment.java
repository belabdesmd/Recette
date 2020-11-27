package com.belfoapps.recette.views.fragments;

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
import com.belfoapps.recette.base.HomeListener;
import com.belfoapps.recette.databinding.SavedFragmentBinding;
import com.belfoapps.recette.models.pojo.Recipe;
import com.belfoapps.recette.ui.adapters.RecipesAdapter;
import com.belfoapps.recette.ui.custom.RecipesItemDecoration;
import com.belfoapps.recette.viewmodels.SavedViewModel;

import java.util.ArrayList;
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
    private HomeListener listener;

    //Observers
    private final Observer<List<Recipe>> recipesObserver = this::initRecyclerView;

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

        //Data Observer
        mViewModel.getRecipesData().observe(getViewLifecycleOwner(), recipesObserver);

        //Refresh Layout
        mBinding.swipeRefreshHome.setRefreshing(true);

        if (savedInstanceState == null) {
            //Load Recipes
            mViewModel.loadRecipes();
        } else {
            //Init RecyclerView
            initRecyclerView(mViewModel.getRecipes());
        }

        //Refresh Listener
        mBinding.swipeRefreshHome.setOnRefreshListener(() -> mViewModel.loadRecipes());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.getRecipesData().removeObserver(recipesObserver);
        mBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void initRecyclerView(List<Recipe> recipes) {
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(COL_NUM, StaggeredGridLayoutManager.VERTICAL);
        RecipesAdapter mAdapter = new RecipesAdapter(new ArrayList<>(recipes), listener, getContext());

        mBinding.recipesRecyclerview.setLayoutManager(mLayoutManager);
        mBinding.recipesRecyclerview.addItemDecoration(new RecipesItemDecoration());
        mBinding.recipesRecyclerview.setAdapter(mAdapter);

        if (recipes != null && !recipes.isEmpty())
            showRecipesList();
        else showNoSavedRecipes();
    }

    public void showNoSavedRecipes() {
        mBinding.errorImage.setImageResource(R.drawable.empty);
        mBinding.errorText.setText(getResources().getString(R.string.empty_error));
        mBinding.error.setVisibility(View.VISIBLE);
        mBinding.savedContent.setVisibility(View.GONE);

        mBinding.swipeRefreshHome.setRefreshing(false);
    }

    public void showRecipesList() {
        mBinding.error.setVisibility(View.GONE);
        mBinding.savedContent.setVisibility(View.VISIBLE);

        mBinding.swipeRefreshHome.setRefreshing(false);
    }
}
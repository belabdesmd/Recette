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
import com.belfoapps.recette.databinding.HomeFragmentBinding;
import com.belfoapps.recette.models.pojo.Recipe;
import com.belfoapps.recette.ui.adapters.RecipesAdapter;
import com.belfoapps.recette.ui.custom.RecipesItemDecoration;
import com.belfoapps.recette.viewmodels.HomeViewModel;
import com.belfoapps.recette.views.MainFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import static android.view.View.GONE;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements MainFragment.HomeDataLoadedListener {
    private static final String TAG = "HomeFragment";
    private static final int COL_NUM = 2;

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private HomeViewModel mViewModel;
    private HomeListener listener;
    private RecipesAdapter mAdapter;
    private HomeFragmentBinding mBinding;
    private boolean error_occurred = false;

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
        try {
            listener = (HomeListener) getParentFragment();
            ((MainFragment) getParentFragment()).setDataLoadedListener(this);
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = HomeFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init ViewModel
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        if (savedInstanceState == null) {
            //Loading UI
            mBinding.shimmerViewContainer.startShimmer();
            //Load Recipes
            mViewModel.getRecipesData().observe(getViewLifecycleOwner(), recipesObserver);
        } else {
            //Init RecyclerView
            initRecyclerView(mViewModel.getRecipes());
        }

        //Load Ads
        mViewModel.loadAd(mBinding.ad);

        //Init Listener
        mBinding.seeMore.setOnClickListener(v -> listener.allRecipes());
        mBinding.swipeRefreshHome.setOnRefreshListener(() -> {
            if (mViewModel.ableToFetchData(requireContext()) || error_occurred)
                mViewModel.refetchData(getViewLifecycleOwner());
            else mBinding.swipeRefreshHome.setRefreshing(false);
        });

        //Demand Access
        listener.demandAccess("Home");
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
    @Override
    public void getData() {
        mViewModel.loadRecipes(true);
    }

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

    @Override
    public void showError() {
        //An Error Occurred
        error_occurred = true;

        mBinding.shimmerViewContainer.stopShimmer();
        mBinding.shimmerViewContainer.setVisibility(GONE);

        mBinding.errorImage.setImageResource(R.drawable.error);
        mBinding.errorText.setText(getResources().getString(R.string.general_error));
        mBinding.error.setVisibility(View.VISIBLE);
        mBinding.swipeRefreshHome.setVisibility(GONE);

        mBinding.swipeRefreshHome.setRefreshing(false);
    }

    public void showRecipesList() {
        //Everything is normal
        error_occurred = false;

        mBinding.shimmerViewContainer.stopShimmer();
        mBinding.shimmerViewContainer.setVisibility(GONE);

        mBinding.error.setVisibility(GONE);
        mBinding.swipeRefreshHome.setVisibility(View.VISIBLE);

        mBinding.swipeRefreshHome.setRefreshing(false);
    }
}
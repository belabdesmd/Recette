package com.belfoapps.recette.views.fragments;

import androidx.lifecycle.ViewModelProvider;

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
import com.belfoapps.recette.databinding.CategoriesFragmentBinding;
import com.belfoapps.recette.models.pojo.Category;
import com.belfoapps.recette.ui.adapters.CategoriesAdapter;
import com.belfoapps.recette.ui.custom.RecipesItemDecoration;
import com.belfoapps.recette.viewmodels.CategoriesViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoriesFragment extends Fragment {
    private static final String TAG = "CategoriesFragment";
    private static final int COL_NUM = 1;

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private CategoriesViewModel mViewModel;
    private CategoriesFragmentBinding mBinding;
    private CategoriesAdapter mAdapter;
    private HomeListener listener;
    private boolean error_occurred = false;

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
        mBinding = CategoriesFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init ViewModel
        mViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        //Init UI
        mBinding.shimmerViewContainer.startShimmer();

        //Get Data
        mViewModel.getCategoriesData().observe(getViewLifecycleOwner(), categories -> {
            if (mAdapter == null)
                initRecyclerView(categories);
            else updateRecyclerView(categories);
        });

        //Refresh Data
        mBinding.swipeRefreshCategories.setOnRefreshListener(() -> {
            if (mViewModel.ableToFetchData(requireContext()) || error_occurred)
                mViewModel.refetchData(getViewLifecycleOwner());
            else mBinding.swipeRefreshCategories.setRefreshing(false);
        });
    }

    public void getData() {
        mViewModel.getCategories(true);
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void initRecyclerView(List<Category> categories) {
        //Declarations
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(COL_NUM, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new CategoriesAdapter(categories, listener, getContext());

        mBinding.categoriesRecyclerview.setLayoutManager(mLayoutManager);
        mBinding.categoriesRecyclerview.addItemDecoration(new RecipesItemDecoration());
        mBinding.categoriesRecyclerview.setAdapter(mAdapter);

        if (!categories.isEmpty())
            showCategoriesList();
        else showError();
    }

    public void updateRecyclerView(List<Category> categories) {
        if (mAdapter != null) {
            //Deleting the List of the Categories
            mAdapter.clearAll();

            // Adding The New List of Categories
            mAdapter.addAll(categories);
        }

        if (!categories.isEmpty())
            showCategoriesList();
        else showError();
    }

    public void showError() {
        //An Error Occurred
        error_occurred = true;

        mBinding.shimmerViewContainer.stopShimmer();
        mBinding.shimmerViewContainer.setVisibility(View.GONE);

        mBinding.errorImage.setImageResource(R.drawable.error);
        mBinding.errorText.setText(getResources().getString(R.string.general_error));
        mBinding.error.setVisibility(View.VISIBLE);
        mBinding.categoriesRecyclerview.setVisibility(View.GONE);

        mBinding.swipeRefreshCategories.setRefreshing(false);
    }

    public void showCategoriesList() {
        //Everything is normal
        error_occurred = false;

        mBinding.shimmerViewContainer.stopShimmer();
        mBinding.shimmerViewContainer.setVisibility(View.GONE);

        mBinding.error.setVisibility(View.GONE);
        mBinding.categoriesRecyclerview.setVisibility(View.VISIBLE);

        mBinding.swipeRefreshCategories.setRefreshing(false);
    }
}
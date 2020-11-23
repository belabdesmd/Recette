package com.belfoapps.recette.views.fragments;

import android.annotation.SuppressLint;
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
import com.belfoapps.recette.databinding.ShoppingsFragmentBinding;
import com.belfoapps.recette.models.pojo.Shopping;
import com.belfoapps.recette.ui.adapters.ShoppingListAdapter;
import com.belfoapps.recette.viewmodels.ShoppingsViewModel;
import com.belfoapps.recette.views.MainActivity;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShoppingsFragment extends Fragment {
    private static final String TAG = "ShoppingsFragment";
    private static final int COL_NUM = 1;

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private ShoppingsViewModel mViewModel;
    private ShoppingsFragmentBinding mBinding;
    private ShoppingListAdapter mAdapter;
    private MainListener listener;

    //Observers
    private final Observer<List<Shopping>> shoppingsObserver = shoppings -> {
        initRecyclerView(shoppings);
        setLastTimeUpdated(mViewModel.getLastTimeUpdated());
    };
    private final Observer<Boolean> removedObserver = removed -> {
        setLastTimeUpdated(mViewModel.getLastTimeUpdated());
        clearRecyclerView();
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
        mBinding = ShoppingsFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(ShoppingsViewModel.class);

        //Init Listener
        initListener();

        //Get Data
        mViewModel.getShoppingData().observe(getViewLifecycleOwner(), shoppingsObserver);
        mViewModel.getShoppings();

        //Cleared Listener
        mViewModel.getRemovedData().observe(getViewLifecycleOwner(), removedObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getShoppingData().removeObserver(shoppingsObserver);
        mViewModel.getRemovedData().removeObserver(removedObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    private void initListener() {
        mBinding.clearIcon.setOnClickListener(v -> {
            mViewModel.clearShoppingList();
            clearRecyclerView();
        });
        mBinding.back.setOnClickListener(v -> listener.goBack());
    }

    @SuppressLint("SetTextI18n")
    public void setLastTimeUpdated(String date) {
        mBinding.dateUpdated.setText(getResources().getString(R.string.last_update) + " " + date);
    }

    public void initRecyclerView(List<Shopping> shopping_list) {
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(COL_NUM, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new ShoppingListAdapter(mViewModel, shopping_list, getContext());

        mBinding.shoppingListRecyclerview.setLayoutManager(mLayoutManager);
        mBinding.shoppingListRecyclerview.setAdapter(mAdapter);

        if (shopping_list != null && !shopping_list.isEmpty())
            showShoppingList();
        else showNoSavedShoppings();
    }

    public void clearRecyclerView() {
        if (mAdapter != null) {
            //Deleting the List of the Categories
            mAdapter.clearAll();
        }
        showNoSavedShoppings();
    }

    public void showNoSavedShoppings() {
        mBinding.errorImage.setImageResource(R.drawable.empty);
        mBinding.errorText.setText(getResources().getString(R.string.empty_error));

        mBinding.error.setVisibility(View.VISIBLE);
        mBinding.shoppingListRecyclerview.setVisibility(View.INVISIBLE);
    }

    public void showShoppingList() {
        mBinding.error.setVisibility(View.GONE);
        mBinding.shoppingListRecyclerview.setVisibility(View.VISIBLE);
    }
}
package com.belfoapps.recette.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.belfoapps.recette.R;
import com.belfoapps.recette.base.HomeListener;
import com.belfoapps.recette.base.MainListener;
import com.belfoapps.recette.databinding.MainFragmentBinding;
import com.belfoapps.recette.ui.adapters.GeneralPagerAdapter;
import com.belfoapps.recette.viewmodels.MainViewModel;
import com.belfoapps.recette.views.fragments.CategoriesFragment;
import com.belfoapps.recette.views.fragments.HomeFragment;
import com.belfoapps.recette.views.fragments.SavedFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@AndroidEntryPoint
public class MainFragment extends Fragment implements HomeListener {
    private static final String TAG = "MainFragment";
    public static int[] tabIcons = {R.drawable.home, R.drawable.categories,
            R.drawable.saved};

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MainViewModel mViewModel;
    private MainListener listener;
    private GeneralPagerAdapter mAdapter;
    private MainFragmentBinding mBinding;
    private HomeDataLoadedListener homeLoadedListener;
    private CategoriesDataLoadedListener categoriesLoadedListener;

    public interface HomeDataLoadedListener {

        void getData();

        void showError();
    }

    public interface CategoriesDataLoadedListener {

        void getData();

        void showError();
    }

    //Observers
    private final Observer<Boolean> canGetObserver = canGet -> {
        if (canGet) {
            homeLoadedListener.getData();
            categoriesLoadedListener.getData();
        }
        else {
            homeLoadedListener.showError();
            categoriesLoadedListener.showError();
        }
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
        mBinding = MainFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        //Init UI
        init();

        //Get Data After Loaded
        mViewModel.getDataLoaded().observe(getViewLifecycleOwner(), canGetObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getDataLoaded().removeObserver(canGetObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setDataLoadedListener(HomeDataLoadedListener loadedListener) {
        this.homeLoadedListener = loadedListener;
    }

    public void setDataLoadedListener(CategoriesDataLoadedListener loadedListener) {
        this.categoriesLoadedListener = loadedListener;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    private void init() {
        setPageName(0);
        initViewPager();

        //Init Listener
        mBinding.shoppingListIcon.setOnClickListener(v -> listener.goToShoppings());
        mBinding.menu.setOnClickListener(v -> listener.openDrawer());
    }

    public void setPageName(int position) {
        if (position == 0) {
            mBinding.pageTitle.setVisibility(GONE);
            mBinding.logo.setVisibility(VISIBLE);
            mBinding.menu.setVisibility(VISIBLE);
        } else {
            mBinding.pageTitle.setVisibility(VISIBLE);
            mBinding.logo.setVisibility(GONE);
            mBinding.menu.setVisibility(GONE);
            mBinding.pageTitle.setText(getResources().getStringArray(R.array.main_tab_titles)[position]);
        }
    }

    public void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        HomeFragment homeFragment = new HomeFragment();
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        SavedFragment savedRecipesFragment = new SavedFragment();

        fragments.add(homeFragment);
        fragments.add(categoriesFragment);
        fragments.add(savedRecipesFragment);

        mAdapter = new GeneralPagerAdapter(getChildFragmentManager(), fragments, getContext());
        mBinding.mainPager.setAdapter(mAdapter);
        mBinding.mainPager.setOffscreenPageLimit(3);

        initTabLayout();
    }

    public void initTabLayout() {
        mBinding.tablayout.setupWithViewPager(mBinding.mainPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < mBinding.tablayout.getTabCount(); i++) {
            TabLayout.Tab tab = mBinding.tablayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(mAdapter.getTabView(i));
        }
        enableTabAt(0);

        //TabLayout Listener
        mBinding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setPageName(tab.getPosition());
                enableTabAt(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void enableTabAt(int x) {
        TabLayout.Tab tab = mBinding.tablayout.getTabAt(x);

        for (int i = 0; i < getResources().getStringArray(R.array.main_tab_titles).length; i++) {
            if (i == x) {
                assert tab != null;
                ((ImageView) Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tab_icon))
                        .setImageResource(tabIcons[i]);
                ImageViewCompat.setImageTintList(Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tab_icon),
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorAccent)));
                ((TextView) tab.getCustomView().findViewById(R.id.tab_title))
                        .setTextColor(getResources().getColor(R.color.primaryTextColor));

            } else {
                TabLayout.Tab tab1 = mBinding.tablayout.getTabAt(i);
                assert tab1 != null;
                ((ImageView) Objects.requireNonNull(tab1.getCustomView()).findViewById(R.id.tab_icon))
                        .setImageResource(tabIcons[i]);
                ImageViewCompat.setImageTintList(Objects.requireNonNull(tab1.getCustomView()).findViewById(R.id.tab_icon),
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.secondaryTextColor)));
                ((TextView) tab1.getCustomView().findViewById(R.id.tab_title)).setTextColor(getResources().getColor(R.color.secondaryTextColor));
            }
        }
    }

    @Override
    public void allRecipes() {
        listener.allRecipes();
    }

    @Override
    public void goToRecipe(Long recipeId) {
        listener.goToRecipe(recipeId);
    }

    @Override
    public void recipesFromCategory(Long id, String name) {
        listener.recipesFromCategory(id, name);
    }
}
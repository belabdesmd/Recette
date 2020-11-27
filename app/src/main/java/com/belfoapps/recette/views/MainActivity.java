package com.belfoapps.recette.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.belfoapps.recette.R;
import com.belfoapps.recette.base.MainListener;
import com.belfoapps.recette.databinding.ActivityMainBinding;
import com.belfoapps.recette.viewmodels.MainViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements MainListener {
    private static final String TAG = "MainActivity";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MainViewModel mViewModel;
    private ActivityMainBinding mBinding;

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set Theme
        setTheme(R.style.Theme_Recette);
        super.onCreate(savedInstanceState);

        //Set ViewBinding
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        //Set ViewModel
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //Init Navigation
        initNavigation();

        //Check GDPR
        mViewModel.checkGDPRConsent(getResources().getBoolean(R.bool.GDPR_Enabled));

        //Init Interstitial Ad
        mViewModel.initInterstitialAd(this);

        //Loading Data
        if (savedInstanceState == null)
            mViewModel.loadData(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    @SuppressLint("NonConstantResourceId")
    private void initNavigation() {
        //Init Navigation
        mBinding.navView.setItemIconTintList(null);

        //Change Titles color
        MenuItem pages = mBinding.navView.getMenu().findItem(R.id.pages);
        MenuItem socials = mBinding.navView.getMenu().findItem(R.id.sociale_media);
        SpannableString s1 = new SpannableString(pages.getTitle());
        SpannableString s2 = new SpannableString(socials.getTitle());
        s1.setSpan(new TextAppearanceSpan(this, R.style.Titles), 0, s1.length(), 0);
        s2.setSpan(new TextAppearanceSpan(this, R.style.Titles), 0, s2.length(), 0);
        pages.setTitle(s1);
        socials.setTitle(s2);
        mBinding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.saved_recipes_menu:
                    ((MainFragment) getSupportFragmentManager().getPrimaryNavigationFragment()
                            .getChildFragmentManager().getPrimaryNavigationFragment())
                            .navigateViewPager(2);
                    break;
                case R.id.categories_menu:
                    ((MainFragment) getSupportFragmentManager().getPrimaryNavigationFragment()
                            .getChildFragmentManager().getPrimaryNavigationFragment())
                            .navigateViewPager(1);
                    break;
                case R.id.shopping_list_menu:
                    goToShoppings();
                    break;
                case R.id.youtube_menu:
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/channel/" + getResources().getString(R.string.youtube_channel_id))));
                    break;
                case R.id.instagram_menu:
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/" + getResources().getString(R.string.instagram_username))));
                    break;
                case R.id.facebook_menu:
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getResources().getString(R.string.facebook_page_link))));
                    break;
                case R.id.twitter_menu:
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.twitter.com/" + getResources().getString(R.string.twitter_id))));
                    break;
            }

            // close drawer when item is tapped
            mBinding.drawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public void openDrawer() {
        mBinding.drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void allRecipes() {
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", -1);
        bundle.putString("categoryName", getResources().getString(R.string.all_recipes));
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.recipes, bundle);
    }

    @Override
    public void recipesFromCategory(Long categoryId, String categoryName) {
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", categoryId);
        bundle.putString("categoryName", categoryName);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.recipes, bundle);
    }

    @Override
    public void goToRecipe(Long recipeId, boolean fromHome) {
        Bundle bundle = new Bundle();
        bundle.putLong("recipeId", recipeId);
        bundle.putBoolean("fromHome", fromHome);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.recipe, bundle);
    }

    @Override
    public void goToShoppings() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.shoppings);
    }

    @Override
    public void goBack() {
        //onBackPressed();
        Navigation.findNavController(this, R.id.nav_host_fragment).popBackStack();

        //Show Interstitial
        if (getResources().getBoolean(R.bool.INTERSTITIAL_AD_Enabled) &&
                mViewModel.ableToShowInterstitial(this))
            mViewModel.showInterstitialAd();
    }

    @Override
    public void backHome() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("back", true);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.back, bundle);

        //Show Interstitial
        if (getResources().getBoolean(R.bool.INTERSTITIAL_AD_Enabled) &&
                mViewModel.ableToShowInterstitial(this))
            mViewModel.showInterstitialAd();
    }
}
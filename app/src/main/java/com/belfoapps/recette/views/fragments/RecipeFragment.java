package com.belfoapps.recette.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.belfoapps.recette.databinding.RecipeFragmentBinding;
import com.belfoapps.recette.models.pojo.Recipe;
import com.belfoapps.recette.models.pojo.Shopping;
import com.belfoapps.recette.ui.adapters.ShoppingListAdapter;
import com.belfoapps.recette.ui.adapters.StepsAdapter;
import com.belfoapps.recette.ui.custom.RecipesItemDecoration;
import com.belfoapps.recette.viewmodels.RecipeViewModel;
import com.belfoapps.recette.views.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipeFragment extends Fragment {
    private static final String TAG = "RecipeFragment";
    public static final String ID = "recipeId";
    public static final String HOME = "fromHome";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private RecipeFragmentBinding mBinding;
    private RecipeViewModel mViewModel;
    private MainListener listener;
    private Long recipeId;
    private boolean fromHome;

    //Callbacks
    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            listener.backHome();
        }
    };

    //Observers
    private final Observer<Recipe> recipeObserver = this::setContent;
    private final Observer<Boolean> bookmarkedObserver = this::setBookmarked;

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getLong(ID);
            fromHome = getArguments().getBoolean(HOME);
        }

        //Going back
        if (fromHome)
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
        mBinding = RecipeFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        //Init
        init();

        //Data Observer
        mViewModel.getRecipeData().observe(getViewLifecycleOwner(), recipeObserver);

        if (savedInstanceState == null) {
            //Load Recipe
            mViewModel.loadRecipe(recipeId);
        } else {
            recipeId = savedInstanceState.getLong(ID);
            fromHome = savedInstanceState.getBoolean(HOME);

            //Set Content
            setContent(mViewModel.getRecipe());
        }

        //Refresh
        mBinding.swipeRefreshRecipe.setOnRefreshListener(() -> {
            //Init UI
            if (getArguments() != null)
                mViewModel.loadRecipe(recipeId);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ID, recipeId);
        outState.putBoolean(HOME, fromHome);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.getRecipeData().removeObserver(recipeObserver);
        mViewModel.getBookmarkedData().removeObserver(bookmarkedObserver);
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
    private void init() {
        //Init UI
        mBinding.back.bringToFront();
        mBinding.backContent.bringToFront();
        mBinding.shimmerViewContainer.setVisibility(View.VISIBLE);
        mBinding.shimmerViewContainer.startShimmer();

        //Load Ads
        mViewModel.loadAd(mBinding.ad1);
        mViewModel.loadAd(mBinding.ad2);

        //Listeners
        mBinding.back.setOnClickListener(v -> {
            if (fromHome)
                listener.backHome();
            else listener.goBack();
        });
        mBinding.backContent.setOnClickListener(v -> {
            if (fromHome)
                listener.backHome();
            else listener.goBack();
        });
        mBinding.bookmark.setOnClickListener(v -> {
            if (v.getTag().equals(0))
                mViewModel.saveRecipe();
            else mViewModel.unSaveRecipe();
        });
        mViewModel.getBookmarkedData().observe(getViewLifecycleOwner(), bookmarkedObserver);
    }

    @SuppressLint("SetTextI18n")
    public void setContent(Recipe recipe) {
        if (recipe != null) {
            //Setup
            mBinding.shimmerViewContainer.stopShimmer();
            mBinding.shimmerViewContainer.setVisibility(View.GONE);

            mBinding.back.setVisibility(View.GONE);
            mBinding.bookmark.bringToFront();
            mBinding.back.bringToFront();
            mBinding.error.setVisibility(View.GONE);
            mBinding.recipeContent.setVisibility(View.VISIBLE);

            //ViewPager
            initIngredientsRecyclerView(recipe.getRecipeIngredients());
            initStepsRecyclerView(recipe.getRecipeSteps());

            //Details
            setBookmarked(mViewModel.isSaved());
            mBinding.recipeDetailsTitle.setText(recipe.getRecipeTitle());
            mBinding.recipeDetailsCategory.setText(recipe.getCategoryTitle());
            mBinding.recipeDetailsTime.setText(recipe.getRecipeTime() + " " + getResources().getString(R.string.min));
            mBinding.recipeDetailsServings.setText(String.valueOf(recipe.getRecipeServings()));

            //Cover
            Glide.with(this)
                    .load(recipe.getRecipeCover())
                    .fitCenter()
                    .error(R.drawable.error_image)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //holder.loading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //holder.loading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mBinding.recipeDetailsCover);

            //Youtube Player
            if (recipe.getRecipeVideoUrl().equals(""))
                mBinding.youtubePlayerView.setVisibility(View.GONE);
            else {
                mBinding.youtubePlayerView.setVisibility(View.VISIBLE);
                getLifecycle().addObserver(mBinding.youtubePlayerView);
                mBinding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(recipe.getRecipeVideoUrl().split("v=")[1].split("&")[0], 0f);
                        youTubePlayer.pause();
                    }
                });
            }

            //Share
            mBinding.share.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://" + getResources().getString(R.string.website)
                        + "?recipeId=" + recipe.getRecipeId());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            });

            //Disable Refreshing (No Need)
            mBinding.swipeRefreshRecipe.setEnabled(false);
        } else {
            //Set Error
            mBinding.shimmerViewContainer.stopShimmer();
            mBinding.shimmerViewContainer.setVisibility(View.GONE);
            mBinding.errorImage.setImageResource(R.drawable.error);
            mBinding.errorText.setText(getResources().getString(R.string.wrong_recipe_error));
            mBinding.error.setVisibility(View.VISIBLE);

            mBinding.recipeContent.setVisibility(View.GONE);
        }
        mBinding.swipeRefreshRecipe.setRefreshing(false);
    }

    private void initIngredientsRecyclerView(ArrayList<String> ingredients) {
        //init Recycler View
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        ArrayList<Shopping> shoppings = new ArrayList<>();
        for (String ingredient :
                ingredients) {
            shoppings.add(new Shopping(null, ingredient, false));
        }

        ShoppingListAdapter mAdapter = new ShoppingListAdapter(mViewModel, shoppings, getContext());

        mBinding.ingredientsRecyclerview.setLayoutManager(mLayoutManager);
        if (mBinding.ingredientsRecyclerview.getItemDecorationCount() == 0)
            mBinding.ingredientsRecyclerview.addItemDecoration(new RecipesItemDecoration());
        mBinding.ingredientsRecyclerview.setAdapter(mAdapter);
    }

    private void initStepsRecyclerView(ArrayList<String> steps) {
        //init Recycler View
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        StepsAdapter mAdapter = new StepsAdapter(steps);

        mBinding.stepsRecyclerview.setLayoutManager(mLayoutManager);
        if (mBinding.stepsRecyclerview.getItemDecorationCount() == 0)
            mBinding.stepsRecyclerview.addItemDecoration(new RecipesItemDecoration());
        mBinding.stepsRecyclerview.setAdapter(mAdapter);
    }

    public void setBookmarked(boolean b) {
        if (b) {
            mBinding.bookmark.setBackgroundResource(R.drawable.bookmarked);
            mBinding.bookmark.setTag(1);
        } else {
            mBinding.bookmark.setBackgroundResource(R.drawable.bookmark);
            mBinding.bookmark.setTag(0);
        }
    }
}
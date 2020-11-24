package com.belfoapps.recette.base;

public interface MainListener {

    void openDrawer();

    void allRecipes();

    void recipesFromCategory(Long categoryId, String categoryName);

    void goToRecipe(Long recipeId);

    void goToShoppings();

    void goBack();

    void backHome();
}

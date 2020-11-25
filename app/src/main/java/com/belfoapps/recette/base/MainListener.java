package com.belfoapps.recette.base;

public interface MainListener {

    void openDrawer();

    void allRecipes();

    void recipesFromCategory(Long categoryId, String categoryName);

    void goToRecipe(Long recipeId, boolean fromHome);

    void goToShoppings();

    void goBack();

    void backHome();
}

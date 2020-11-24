package com.belfoapps.recette.base;

public interface HomeListener {

    void goToRecipe(Long recipeId);

    void recipesFromCategory(Long id, String name);

    void allRecipes();

    void demandAccess(String home);
}

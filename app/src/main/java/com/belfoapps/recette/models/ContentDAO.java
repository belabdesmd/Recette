package com.belfoapps.recette.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.belfoapps.recette.models.pojo.Category;
import com.belfoapps.recette.models.pojo.Recipe;
import com.belfoapps.recette.models.pojo.Shopping;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ContentDAO {

    /************************************** Inserts *********************************************
     ********************************************************************************************/
    //Recipes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertRecipes(ArrayList<Recipe> recipes);

    //Categories
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCategory(ArrayList<Category> categories);

    //Shopping
    @Insert
    public void insertShopping(Shopping shopping);

    /************************************** Deletes *********************************************
     ********************************************************************************************/
    @Delete
    public void removeShopping(Shopping shopping);

    /************************************** Queries *********************************************
     ********************************************************************************************/
    @Query("SELECT * FROM RECIPES " +
            "INNER JOIN (SELECT id as categoryId, categoryTitle FROM CATEGORIES) ON categoryId = recipeCategory " +
            "LIMIT 6")
    public List<Recipe> getLimitedRecipes();

    @Query("SELECT * FROM RECIPES " +
            "INNER JOIN (SELECT id as categoryId, categoryTitle FROM CATEGORIES) ON categoryId = recipeCategory ")
    public List<Recipe> getRecipes();

    @Query("SELECT * FROM RECIPES " +
            "INNER JOIN (SELECT id as categoryId, categoryTitle FROM CATEGORIES) ON categoryId = recipeCategory " +
            "WHERE recipeId = :id")
    public Recipe getRecipe(Long id);

    @Query("SELECT * FROM RECIPES " +
            "INNER JOIN (SELECT id as categoryId, categoryTitle FROM CATEGORIES) ON categoryId = recipeCategory " +
            "WHERE recipeCategory = :category")
    public List<Recipe> getRecipesByCategory(Long category);

    @Query("SELECT * FROM CATEGORIES")
    public List<Category> getCategories();

    @Query("SELECT * FROM SHOPPINGS")
    public List<Shopping> getShoppings();
}

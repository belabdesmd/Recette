package com.belfoapps.recette.models.pojo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Objects;

@Entity(tableName = "recipes")
public class Recipe implements Cloneable {

    @PrimaryKey
    private long recipeId;
    private String recipeTitle;
    private String recipeCover;
    private Long recipeCategory;
    @ColumnInfo(defaultValue = "")
    private String categoryTitle;
    private String recipeVideoUrl;
    private int recipeTime;
    private int recipeServings;
    private ArrayList<String> recipeIngredients;
    private ArrayList<String> recipeSteps;

    public Recipe(Long recipeId, String recipeTitle, String recipeCover, Long recipeCategory,
                  String recipeVideoUrl, int recipeTime, int recipeServings,
                  ArrayList<String> recipeIngredients, ArrayList<String> recipeSteps) {
        this.recipeId = recipeId;
        this.recipeTitle = recipeTitle;
        this.recipeCover = recipeCover;
        this.recipeCategory = recipeCategory;
        this.recipeVideoUrl = recipeVideoUrl;
        this.recipeTime = recipeTime;
        this.recipeServings = recipeServings;
        this.recipeIngredients = recipeIngredients;
        this.recipeSteps = recipeSteps;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public String getRecipeCover() {
        return recipeCover;
    }

    public void setRecipeCover(String recipeCover) {
        this.recipeCover = recipeCover;
    }

    public Long getRecipeCategory() {
        return recipeCategory;
    }

    public void setRecipeCategory(Long recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    public String getRecipeVideoUrl() {
        return recipeVideoUrl;
    }

    public void setRecipeVideoUrl(String recipeVideoUrl) {
        this.recipeVideoUrl = recipeVideoUrl;
    }

    public int getRecipeTime() {
        return recipeTime;
    }

    public void setRecipeTime(int recipeTime) {
        this.recipeTime = recipeTime;
    }

    public int getRecipeServings() {
        return recipeServings;
    }

    public void setRecipeServings(int recipeServings) {
        this.recipeServings = recipeServings;
    }

    public ArrayList<String> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(ArrayList<String> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public ArrayList<String> getRecipeSteps() {
        return recipeSteps;
    }

    public void setRecipeSteps(ArrayList<String> recipeSteps) {
        this.recipeSteps = recipeSteps;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(recipeId, recipe.recipeId);
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

package com.belfoapps.recette.models.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey
    private Long id;

    private String categoryTitle;
    private String categoryCover;

    public Category(Long id, String categoryTitle, String categoryCover) {
        this.id = id;
        this.categoryTitle = categoryTitle;
        this.categoryCover = categoryCover;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryCover() {
        return categoryCover;
    }

    public void setCategoryCover(String categoryCover) {
        this.categoryCover = categoryCover;
    }
}

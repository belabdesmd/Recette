package com.belfoapps.recette.base;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.belfoapps.recette.models.ContentDAO;
import com.belfoapps.recette.models.pojo.Category;
import com.belfoapps.recette.models.pojo.Recipe;
import com.belfoapps.recette.models.pojo.Shopping;
import com.belfoapps.recette.utils.ListToStringConverter;

@Database(entities = {Recipe.class, Category.class, Shopping.class}, version = 4)
@TypeConverters(ListToStringConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContentDAO contentDAO();
}

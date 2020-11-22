package com.belfoapps.recette.models.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "shoppings")
public class Shopping {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String shopping;
    private boolean checked;

    public Shopping(Long id, String shopping, boolean checked) {
        this.id = id;
        this.shopping = shopping;
        this.checked = checked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopping() {
        return shopping;
    }

    public void setShopping(String shopping) {
        this.shopping = shopping;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shopping shopping1 = (Shopping) o;
        return Objects.equals(shopping, shopping1.shopping);
    }
}

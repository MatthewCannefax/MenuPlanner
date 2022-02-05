package com.matthewcannefax.menuplanner.recipe;

import androidx.room.Ignore;

import com.matthewcannefax.menuplanner.grocery.GroceryCategory;

import java.util.Objects;

public abstract class BaseItem {
    @Ignore
    protected Measurement measurement;
    protected String name;
    protected GroceryCategory category;
    protected boolean itemChecked = false;

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroceryCategory getCategory() {
        return category;
    }

    public void setCategory(GroceryCategory category) {
        this.category = category;
    }

    public boolean isItemChecked() {
        return itemChecked;
    }

    public void setItemChecked(boolean itemChecked) {
        this.itemChecked = itemChecked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseItem baseItem = (BaseItem) o;
        return itemChecked == baseItem.itemChecked && Objects.equals(measurement, baseItem.measurement) && Objects.equals(name, baseItem.name) && category == baseItem.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(measurement, name, category, itemChecked);
    }
}

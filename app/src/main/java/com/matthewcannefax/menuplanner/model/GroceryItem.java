package com.matthewcannefax.menuplanner.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;

//this object class is grocery items including, but not limited to, recipe ingredients

public class GroceryItem implements Parcelable {

    //the name of the grocery item
    private String name;

    //the category of the grocery item using the GroceryCategory enum
    private GroceryCategory category;//category will be as it relates to a grocery store (dairy, meat, etc)

    //default constructor
    public GroceryItem() {
    }

    //constructor that takes both props
    public GroceryItem(String name, GroceryCategory category) {
        this.name = name;
        this.category = category;
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



    //this current does the same thing as getName()
    @Override
    public String toString() {
        return String.format("%s", name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.category == null ? -1 : this.category.ordinal());
    }

    protected GroceryItem(Parcel in) {
        this.name = in.readString();
        int tmpCategory = in.readInt();
        this.category = tmpCategory == -1 ? null : GroceryCategory.values()[tmpCategory];
    }

    public static final Parcelable.Creator<GroceryItem> CREATOR = new Parcelable.Creator<GroceryItem>() {
        @Override
        public GroceryItem createFromParcel(Parcel source) {
            return new GroceryItem(source);
        }

        @Override
        public GroceryItem[] newArray(int size) {
            return new GroceryItem[size];
        }
    };
}

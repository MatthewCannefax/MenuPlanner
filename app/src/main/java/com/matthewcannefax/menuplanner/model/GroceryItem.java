package com.matthewcannefax.menuplanner.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;

//this object class is grocery items including, but not limited to, recipe ingredients

public class GroceryItem implements Parcelable {

    //the name of the grocery item
    private final String name;

    //the category of the grocery item using the GroceryCategory enum
    private final GroceryCategory category;//category will be as it relates to a grocery store (dairy, meat, etc)

    //this current does the same thing as getName()
    @Override
    public String toString() {
        return String.format("%s", name);
    }


    //region Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.category == null ? -1 : this.category.ordinal());
    }

    private GroceryItem(Parcel in) {
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
    //endregion
}

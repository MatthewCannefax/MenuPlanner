package com.matthewcannefax.menuplanner.addEdit;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;

public class RecipeDetailHeaderRowViewHolder extends RecipeDetailViewHolder {

    private TextView heading;

    public RecipeDetailHeaderRowViewHolder(@NonNull View itemView) {
        super(itemView);
        this.heading = itemView.findViewById(R.id.tvCategory);
    }

    @Override
    public void bind(RecipeDetailListRow item) {
        bind((RecipeDetailHeadingRow) item);
    }

    private void bind(RecipeDetailHeadingRow item) {
        heading.setText(item.getHeading());
    }
}

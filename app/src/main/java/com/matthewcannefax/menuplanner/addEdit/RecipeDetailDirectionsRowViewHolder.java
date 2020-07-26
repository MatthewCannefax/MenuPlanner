package com.matthewcannefax.menuplanner.addEdit;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.matthewcannefax.menuplanner.R;

public class RecipeDetailDirectionsRowViewHolder extends RecipeDetailViewHolder {

    private EditText directions;
    private DirectionsChangedListener directionsChangedListener;

    public RecipeDetailDirectionsRowViewHolder(@NonNull View itemView, DirectionsChangedListener directionsChangedListener) {
        super(itemView);
        this.directions = itemView.findViewById(R.id.directionsMultilineEditText);
        this.directionsChangedListener = directionsChangedListener;
    }

    @Override
    public void bind(RecipeDetailListRow item) {
        bind((RecipeDetailDirectionsRow) item);
    }

    private void bind(RecipeDetailDirectionsRow item) {
        directions.setText(item.getDirections());
        directions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                directionsChangedListener.directionsChanged(editable.toString());
            }
        });
    }
}

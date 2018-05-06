package com.matthewcannefax.menuplanner.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.utils.NavDrawer;
import com.matthewcannefax.menuplanner.utils.ShareHelper;

public class OptionsActivity extends AppCompatActivity {

    Button importBTN;
    Button exportBTN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_layout);

        importBTN = findViewById(R.id.importBTN);
        exportBTN = findViewById(R.id.exportBTN);


        setupButtonListeners();

        NavDrawer.setupNavDrawer(OptionsActivity.this, this);
    }

    private void setupButtonListeners(){
        final Context context = this;
        importBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareHelper.importCookbook(OptionsActivity.this, context);
            }
        });

        exportBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareHelper.sendRecipes(context);
            }
        });
    }
}

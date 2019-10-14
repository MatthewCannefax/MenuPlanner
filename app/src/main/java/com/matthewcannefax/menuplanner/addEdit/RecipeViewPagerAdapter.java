package com.matthewcannefax.menuplanner.addEdit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.grocery.GroceryCategory;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ViewPagerHelper;

import java.util.Locale;

public class RecipeViewPagerAdapter extends PagerAdapter {

    private final Context mContext;
    private final int mTabPosition;
    private final Recipe mRecipe;
    private EditText etDirections;
//    private ListView listView;
    private RecyclerView recyclerView;

    public RecipeViewPagerAdapter(Context context, Recipe recipe, int tabPosition){
        mContext = context;
        mRecipe = recipe;
        mTabPosition = tabPosition;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @SuppressLint("InflateParams")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        switch (mTabPosition){

                case 0:
                    LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert mLayoutInflater != null;
                    view = mLayoutInflater.inflate(R.layout.ingredient_recyclerview_layout, null);
                recyclerView = view.findViewById(R.id.ingredientRecyclerView);

                if(mRecipe.getIngredientList() != null && mRecipe.getIngredientList().size() != 0) {
//                    IngredientItemAdapter ingredientItemAdapter = new IngredientItemAdapter(mContext, mRecipe.getIngredientList());
                    IngredientRecyclerAdapter recyclerAdapter = new IngredientRecyclerAdapter(mContext, mRecipe.getIngredientList(), mRecipe);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//                    listView.setAdapter(ingredientItemAdapter);
                }

                    Button mAddIngredientButton = view.findViewById(R.id.ingredient_layout_button);

                ViewPagerHelper.setAddIngredientButton(mContext, mAddIngredientButton, mRecipe, recyclerView);

                break;

                case 1:
                mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert mLayoutInflater != null;
                    view = mLayoutInflater.inflate(R.layout.directions_multiline_layout, null);
                etDirections = view.findViewById(R.id.directionsMultilineEditText);
                etDirections.setText(mRecipe.getDirections());

                etDirections.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        mRecipe.setDirections(etDirections.getText().toString());
                    }
                });
                break;

                default:
                    mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert mLayoutInflater != null;
                    view = mLayoutInflater.inflate(R.layout.directions_multiline_layout, null);
                    EditText editText2 = view.findViewById(R.id.directionsMultilineEditText);
                    editText2.setText("");
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
        }

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);



        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;

        vp.removeView(view);
    }
}

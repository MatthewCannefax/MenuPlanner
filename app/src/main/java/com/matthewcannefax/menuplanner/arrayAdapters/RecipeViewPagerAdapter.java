package com.matthewcannefax.menuplanner.arrayAdapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.ViewPagerHelper;

public class RecipeViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int mTabPosition;
    private LayoutInflater mLayoutInflater;
    private Recipe mRecipe;
    private Button mAddIngredientButton;
    private EditText etDirections;
    private boolean enabled;

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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        switch (mTabPosition){

                case 0:
                mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = mLayoutInflater.inflate(R.layout.ingredient_listview_layout, null);
                ListView listView = view.findViewById(R.id.ingredientListView);
                IngredientItemAdapter ingredientItemAdapter = new IngredientItemAdapter(mContext, mRecipe.getIngredientList());
                listView.setAdapter(ingredientItemAdapter);
                mAddIngredientButton = view.findViewById(R.id.ingredient_layout_button);

                ViewPagerHelper.setAddIngredientButton(mContext, view, mAddIngredientButton, mRecipe, ingredientItemAdapter, listView);

                break;

                case 1:
                mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

//        //check that the directions layout is the one being destroyed
//        //checking for RelativeLayout, probably not the best way to do this!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        if(view instanceof RelativeLayout){
//            saveDirections(view);
//        }


        vp.removeView(view);
    }

//    public void saveDirections(View view){
//        EditText editText = view.findViewById(R.id.directionsMultilineEditText);
//        mRecipe.setDirections(editText.getText().toString());
//    }
}

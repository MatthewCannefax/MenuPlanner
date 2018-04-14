package com.matthewcannefax.menuplanner.arrayAdapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

    public RecipeViewPagerAdapter(Context context, Recipe recipe, int tabPosition){
        mContext = context;
        mRecipe = recipe;
        mTabPosition = tabPosition;
    }

    @Override
    public int getCount() {
        return 2;
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
                EditText editText = view.findViewById(R.id.directionsMultilineEditText);
                editText.setText(mRecipe.getDirections());
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
        vp.removeView(view);
    }
}

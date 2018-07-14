package com.matthewcannefax.menuplanner.arrayAdapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.NumberHelper;
import com.matthewcannefax.menuplanner.utils.ViewPagerHelper;

public class RecipeViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int mTabPosition;
    private Recipe mRecipe;
    private EditText etDirections;
    private boolean enabled;
    private ListView listView;
    private IngredientItemAdapter ingredientItemAdapter;

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
                view = mLayoutInflater.inflate(R.layout.ingredient_listview_layout, null);
                listView = view.findViewById(R.id.ingredientListView);

                if(mRecipe.getIngredientList() != null && mRecipe.getIngredientList().size() != 0) {
                    ingredientItemAdapter = new IngredientItemAdapter(mContext, mRecipe.getIngredientList());
                    listView.setAdapter(ingredientItemAdapter);
                }

                    Button mAddIngredientButton = view.findViewById(R.id.ingredient_layout_button);

                ViewPagerHelper.setAddIngredientButton(mContext, view, mAddIngredientButton, mRecipe, ingredientItemAdapter, listView);
                listViewClickListener();

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
//                etDirections.setFocusable(false);
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


    private void listViewClickListener(){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit Ingredient");

                View editIngredientView = LayoutInflater.from(mContext).inflate(R.layout.add_ingredient_item,
                        (ViewGroup)view.findViewById(android.R.id.content), false);

                //controls inside the view
                final EditText etAmount = editIngredientView.findViewById(R.id.amountText);
                final Spinner spMeasure = editIngredientView.findViewById(R.id.amountSpinner);
                final EditText etName = editIngredientView.findViewById(R.id.ingredientName);
                final Spinner spCat = editIngredientView.findViewById(R.id.categorySpinner);

                //setup the default array adapters for the category and measurementtype spinners
                ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, MeasurementType.values());
                ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, GroceryCategory.getEnumIngredients());

                Ingredient ingredient = mRecipe.getIngredientList().get(i);

                etAmount.setText(Double.toString(ingredient.getMeasurement().getAmount()));
                etName.setText(ingredient.getName());

                //set the spinner adpaters
                spMeasure.setAdapter(measureAdapter);
                spCat.setAdapter(ingredCatAdapter);

                spCat.setSelection(GroceryCategory.getCatPosition(ingredient.getCategory()));
                spMeasure.setSelection(ingredient.getMeasurement().getType().ordinal());

                builder.setView(editIngredientView);

                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int n) {
                        mRecipe.getIngredientList().get(i).setName(etName.getText().toString());
                        mRecipe.getIngredientList().get(i).setCategory((GroceryCategory) spCat.getSelectedItem());
                        mRecipe.getIngredientList().get(i).setMeasurement(new Measurement(Double.parseDouble(etAmount.getText().toString()),
                                                                                            (MeasurementType)spMeasure.getSelectedItem()));
                    }
                });
                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int n) {
                            mRecipe.getIngredientList().remove(i);
//                            IngredientItemAdapter ingredientItemAdapter = (IngredientItemAdapter) listView.getAdapter();
                        ((IngredientItemAdapter)(listView.getAdapter())).notifyDataSetChanged();
//                            ingredientItemAdapter.notifyDataSetChanged();
                    }
                });

                builder.show();
                return false;
            }
        });
    }
}

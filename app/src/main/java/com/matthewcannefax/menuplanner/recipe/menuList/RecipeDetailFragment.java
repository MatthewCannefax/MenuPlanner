package com.matthewcannefax.menuplanner.recipe.menuList;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.IngredientRecyclerAdapter;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailFragment extends Fragment {

    private TextView tvName;
    private ImageView imageView;
    private TextView tvCategory;
    private RecyclerView rvIngredients;
    private TextView tvDirections;

    private Recipe mRecipe;

    private DataSource mDataSource;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mDataSource = new DataSource(getActivity());
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey(MenuListActivity.RECIPE_ID_STRING)){
            int selectedID = getArguments().getInt(MenuListActivity.RECIPE_ID_STRING);
            mRecipe = mDataSource.getSpecificRecipe(selectedID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        if(mRecipe != null){
            tvName = rootView.findViewById(R.id.recipe_name_textview);
            imageView = rootView.findViewById(R.id.recipe_imageview);
            tvCategory = rootView.findViewById(R.id.category_textview);
            rvIngredients = rootView.findViewById(R.id.ingredient_recyclerview);
            tvDirections = rootView.findViewById(R.id.directions_textview);

            tvName.setText(mRecipe.getName());

            if(mRecipe.getImagePath() != null && !mRecipe.getImagePath().equals("")){
                ImageHelper.setImageViewDrawable(mRecipe.getImagePath(), getActivity(), imageView);
            }else{
                ImageHelper.setImageViewDrawable("", getActivity(), imageView);
            }

            tvCategory.setText(String.format("%s%s", getActivity().getString(R.string.category_textview), mRecipe.getCategory()));

            IngredientRecyclerAdapter adapter = new IngredientRecyclerAdapter(getActivity(), mRecipe.getIngredientList(), mRecipe);
            rvIngredients.setAdapter(adapter);
            rvIngredients.setLayoutManager(new GridLayoutManager(getActivity(), 2));

            tvDirections.setText(mRecipe.getDirections());
        }
         return rootView;
    }

    public static RecipeDetailFragment newInstance(int selectedRecipe){
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(MenuListActivity.RECIPE_ID_STRING, selectedRecipe);
        fragment.setArguments(arguments);
        return fragment;
    }

}

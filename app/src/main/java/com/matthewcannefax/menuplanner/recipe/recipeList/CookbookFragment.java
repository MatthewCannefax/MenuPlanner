package com.matthewcannefax.menuplanner.recipe.recipeList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.matthewcannefax.menuplanner.BaseFragment;
import com.matthewcannefax.menuplanner.MainViewModel;
import com.matthewcannefax.menuplanner.MenuApplication;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.databinding.FragmentCookbookBinding;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.utils.ShareHelper;

import java.util.List;

//This activity is to display the total list of recipes from the db
public class CookbookFragment extends BaseFragment {

    private List<Recipe> recipeList;
    private RecipeRecyclerAdapter recyclerAdapter;
    private MainViewModel viewModel;
    private FragmentCookbookBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MenuApplication) requireActivity().getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        recipeList = viewModel.getCookbook();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_cookbook, null, false);
        requireActivity().setTitle("Cookbook");//TODO need string resource

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.category_spinner_item, viewModel.getCookbookCategories());
        catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
        binding.catSpinner.setAdapter(catSpinnerAdapter);
        setRecipeListAdapter();

        setFabListener();
    }

    private void setFabListener(){
        binding.fab.setOnClickListener(view -> {
            if (recipeList.stream().anyMatch(Recipe::isItemChecked)) {
                //loop through the recipe list and add the selected recipes to the menu
                for(int position = 0; position < recipeList.size(); position++){
                    if(recipeList.get(position).isItemChecked()){
                        recipeList.get(position).setItemChecked(false);
                        viewModel.addRecipeToMenu(recipeList.get(position).getRecipeID());
                    }
                }
                requireActivity().onBackPressed();
            } else {
                Snackbar.make(requireContext(), requireView(), getString(R.string.no_recipes_selected), Snackbar.LENGTH_LONG).show();
            }
        });
    }

//    private void setFilterBTNListener(final Context mContext, Button filterBTN) {
//        filterBTN.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//               RecipeCategory selectedCat = (RecipeCategory)catSpinner.getSelectedItem();
//               recipeList = mDataSource.getFilteredRecipes(selectedCat);
////               RecipeListItemAdapter filteredAdapter = new RecipeListItemAdapter(mContext, recipeList);
//               RecipeRecyclerAdapter filteredAdapter = new RecipeRecyclerAdapter(mContext, recipeList);
//               recyclerView.setAdapter(filteredAdapter);
//               recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//           }
//        });
//    }

    private void setRecipeListAdapter(){
        if (recipeList != null){
            //instantiate the RecipeMenuItemAdapter passing the total list of recipes
            recyclerAdapter = new RecipeRecyclerAdapter(requireContext(), recipeList);

            //set the RecipeMenuItemAdapter as the adapter for the listview
            binding.recipeRecyclerView.setAdapter(recyclerAdapter);
            binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }else {
//            Toast.makeText(this, "No Recipes Found", Toast.LENGTH_SHORT).show();
            Snackbar.make(requireContext(), requireView(), getString(R.string.no_recipes_found), Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        if(binding.catSpinner.getSelectedItemPosition() != 0) {
            recipeList = viewModel.getRecipesByCategory((RecipeCategory) binding.catSpinner.getSelectedItem());
        }else{
            recipeList = viewModel.getCookbook();
        }
        recyclerAdapter = new RecipeRecyclerAdapter(requireContext(), recipeList);
        binding.recipeRecyclerView.setAdapter(recyclerAdapter);
        binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    //this overridden method creates the menu item in the actionbar
//    @SuppressLint("RestrictedApi")
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//
//        //add the menu button to add recipes
//        MenuInflater menuInflater = getMenuInflater();
//
//        //using the menu layout create specifically for this activity
//        menuInflater.inflate(R.menu.recipe_activity_menu, menu);
//
//        //the add recipes to the menu menuitem
////        MenuItem item = menu.findItem(R.id.addRecipesMenuItem);
//        MenuItem exportItem = menu.findItem(R.id.exportCookbook);
//        MenuItem importItem = menu.findItem(R.id.importCookbook);
//
//        //if this is simply the My Recipes version of the activity, do not show the add recipes menuitem
//        if (!title.equals(getString(R.string.add_to_menu))) {
////            item.setVisible(false);
//            fab.setVisibility(View.INVISIBLE);
//        }else{
//            exportItem.setVisible(false);
//            importItem.setVisible(false);
//        }
//
//        return true;
//    }

    //this overridden method is to handle the actionbar item clicks
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        boolean b;
//
//        switch(item.getItemId()){
//            case android.R.id.home:
//                NavDrawer.navDrawerOptionsItem(binding.drawerLayout);
//                return true;
//            //remove the select items from the recipelist
//            case R.id.removeRecipes:
//                //this var and the loop checks if there are any recipes selected
//                boolean anySelected = anySelected();
//
//                //use the alert dialog to check if the user truly wants to delete the selected recipes
//                AlertDialog.Builder builder;
//                if (anySelected) {
//                    builder = new AlertDialog.Builder(requireContext());
//                    builder.setTitle(R.string.delete_complete);
//                    builder.setMessage(R.string.permanently_delete);
//                    builder.setNegativeButton(R.string.cancel, null);
//                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                            //loop through the recipes to delete only the recipes that are selected
//                            for (int position = 0; position < recipeList.size(); position++) {
//                                if (recipeList.get(position).isItemChecked()) {
//                                    viewModel.removeRecipe(recipeList.get(position));
//                                }
//                            }
//
//                            if (viewModel.getCookbook() != null && viewModel.getCookbook().size() != 0) {
//                                //reset the adapter
//                                recyclerAdapter = new RecipeRecyclerAdapter(requireContext(), viewModel.getCookbook());
//                                binding.recipeRecyclerView.setAdapter(recyclerAdapter);
//                                binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//                                //save the newly edited recipe list
//                                recipeList = viewModel.getCookbook();
//                                Snackbar.make(requireContext(), requireView(), getString(R.string.removed), Snackbar.LENGTH_LONG).show();
//                            } else {
//                                requireActivity().onBackPressed();
//                                Snackbar.make(requireContext(), requireView(), getString(R.string.removed), Snackbar.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                    builder.show();
//
//                } else{
//                    Snackbar.make(requireContext(), requireView(), getString(R.string.no_recipes_selected), Snackbar.LENGTH_LONG).show();
//                }
//                b = true;
//                break;
//            case R.id.importCookbook:
//                ShareHelper.importCookbook(requireActivity());
//                b = true;
//                break;
//            case R.id.exportCookbook:
//                anySelected = anySelected();
//                if(anySelected){
//                    //loop through the recipe list and add the selected recipes to send
//                    List<Recipe> sendRecipes = new ArrayList<>();
//                    for(int position = 0; position < recipeList.size(); position++){
//                        if(recipeList.get(position).isItemChecked()){
//                            recipeList.get(position).setItemChecked(false);
//                            sendRecipes.add(recipeList.get(position));
//                        }
//                    }
//                    ShareHelper.sendRecipeSelection(requireContext(), sendRecipes);
//                }else{
//                    Snackbar.make(requireContext(), requireView(), getString(R.string.select_recipes_to_share), Snackbar.LENGTH_LONG).show();
//                }
//                b = true;
//                break;
//            case R.id.help:
//                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(requireContext());
//                helpBuilder.setTitle(R.string.help);
//                helpBuilder.setMessage(R.string.recipe_list_help);
//                helpBuilder.setNeutralButton(R.string.ok, null);
//                helpBuilder.show();
//                b = true;
//                break;
//                default:
//                    b = false;
//        }
//
//        return b;
//
//    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        ShareHelper.activityResultImportCookbook(requireContext(), requireActivity(), requestCode, resultCode, data);
    }
}

package com.example.exe2txt;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ProductRepository productRepository;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private View view;
//    private Toolbar toolbar;

    private  AppDatabase appDatabase;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);


        progressBar = view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        // UI COMPONENTS

        //Setup For ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);


        setupMenu();


        recyclerView = view.findViewById(R.id.recyclerViewhome);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productRepository = new ProductRepository(getContext());
        adapter = new ProductAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        productRepository.getAllProducts().observe(getViewLifecycleOwner(), productEntities -> {
            if (productEntities != null && !productEntities.isEmpty()) {
                adapter.updateList(productEntities);
                progressBar.setVisibility(View.GONE);
            } else {
                fetchAndStoreProducts(true);
            }
        });

        BottomNavigationView bottomNavigationView;

        SearchView searchView = view.findViewById(R.id.searchView);

        bottomNavigationView = getActivity().findViewById(R.id.navagation_bar);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (adapter != null) {
                    adapter.filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (adapter != null) {
                    adapter.filter(newText);
                }
                return false;
            }
        });


        appDatabase=AppDatabase.getInstance(requireContext());





        appDatabase.cartDao().getAllCart().observe(getViewLifecycleOwner(),carts -> {

            int itemCount=0;

            for(CartEntity cart: carts){

                itemCount+=cart.getQuantity();
            }


            if(getActivity() != null){





                if(bottomNavigationView!= null){

                    BadgeDrawable badge=bottomNavigationView.getOrCreateBadge(R.id.nav_cart);

                    if(itemCount>0){
                        badge.setVisible(true);
                        badge.setNumber(itemCount);
                    }else {

                        badge.clearNumber();
                        badge.setVisible(false);
                    }

                }




            }


        });


        return view;
    }

    private void fetchAndStoreProducts(boolean showSuccessMessage) {
        progressBar.setVisibility(View.VISIBLE);
        productRepository.fetchProducts(requireContext(), view, new RepositoryCallback<List<ProductEntity>>() {
            @Override
            public void onSucess(List<ProductEntity> productList) {

                requireActivity().runOnUiThread(() -> {

                    progressBar.setVisibility(View.GONE);

                    if (productList != null && !productList.isEmpty()) {
                        adapter.updateList(productList);

                        if (showSuccessMessage) {
                            Toast.makeText(requireContext(), "New products loaded!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }

            @Override
            public void onError(String errorMessage) {

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error in Response from APi", Toast.LENGTH_SHORT).show();
                });

            }
        });

    }

    private void setupMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu);


                MenuItem refreshItem = menu.findItem(R.id.action_refresh);
                if (refreshItem != null) {
                    Drawable icon = refreshItem.getIcon();
                    if (icon != null) {
                        icon.setTint((ContextCompat.getColor(requireContext(), R.color.white)));
                    }
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.action_refresh) {
                    refreshData();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }

    private void refreshData() {

        Toast.makeText(requireContext(), "Refreshing Product List", Toast.LENGTH_SHORT).show();

        new Thread(() -> {

            AppDatabase db = AppDatabase.getInstance(requireContext());

            db.productDao().clearTable();
            db.orderDao().clearTable();
            db.cartDao().clearTable();
            db.orderItemDao().clearTable();
            requireActivity().runOnUiThread(() -> {
                adapter.updateList(new ArrayList<>());

                fetchAndStoreProducts(true);

            });
        }).start();


    }
}

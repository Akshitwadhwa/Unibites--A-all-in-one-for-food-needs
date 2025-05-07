package com.example.unibites;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private static final String TAG = "FoodFragment";

    // RecyclerViews for each category
    private RecyclerView recyclerViewBreakfast, recyclerViewLunch, recyclerViewSnacks, recyclerViewDinner;

    // Adapters for each RecyclerView
    private ProductAdapter breakfastAdapter, lunchAdapter, snacksAdapter, dinnerAdapter;

    // Lists for each category
    private List<Product> breakfastList = new ArrayList<>();
    private List<Product> lunchList = new ArrayList<>();
    private List<Product> snacksList = new ArrayList<>();
    private List<Product> dinnerList = new ArrayList<>();

    private FirebaseFirestore db;

    public FoodFragment() {}

    public static FoodFragment newInstance(String param1, String param2) {
        FoodFragment fragment = new FoodFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        // Bind RecyclerViews
        recyclerViewBreakfast = view.findViewById(R.id.recyclerViewBreakfast);
        recyclerViewLunch = view.findViewById(R.id.recyclerViewLunch);
        recyclerViewSnacks = view.findViewById(R.id.recyclerViewSnacks);
        recyclerViewDinner = view.findViewById(R.id.recyclerViewDinner);

        // Setup RecyclerViews with GridLayout
        setupRecyclerView(recyclerViewBreakfast, breakfastList, breakfastAdapter = new ProductAdapter(getContext(), breakfastList, this));
        setupRecyclerView(recyclerViewLunch, lunchList, lunchAdapter = new ProductAdapter(getContext(), lunchList, this));
        setupRecyclerView(recyclerViewSnacks, snacksList, snacksAdapter = new ProductAdapter(getContext(), snacksList, this));
        setupRecyclerView(recyclerViewDinner, dinnerList, dinnerAdapter = new ProductAdapter(getContext(), dinnerList, this));

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView, List<Product> list, ProductAdapter adapter) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchAllProductsFromFirestore();
    }

    private void fetchAllProductsFromFirestore() {
        db.collection("Products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear all lists
                        breakfastList.clear();
                        lunchList.clear();
                        snacksList.clear();
                        dinnerList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                String id = document.getId();
                                String name = document.getString("Productname");
                                Double price = document.getDouble("ProductPrice");
                                String description = document.getString("description");
                                String imageUrl = document.getString("Productimage");
                                String category = document.getString("ProductCategory");

                                Product product = new Product(id, name, price, description, imageUrl, category);

                                // Filter products by category
                                switch (category.toLowerCase()) {
                                    case "breakfast":
                                        breakfastList.add(product);
                                        break;
                                    case "lunch":
                                        lunchList.add(product);
                                        break;
                                    case "snacks":
                                        snacksList.add(product);
                                        break;
                                    case "dinner":
                                        dinnerList.add(product);
                                        break;
                                    default:
                                        Log.w(TAG, "Unknown category: " + category);
                                }

                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing product", e);
                            }
                        }

                        // Notify adapters
                        breakfastAdapter.updateProductList(breakfastList);
                        lunchAdapter.updateProductList(lunchList);
                        snacksAdapter.updateProductList(snacksList);
                        dinnerAdapter.updateProductList(dinnerList);

                    } else {
                        Log.e(TAG, "Error fetching products", task.getException());
                        Toast.makeText(getContext(), "Failed to fetch products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onProductClick(Product product, int position) {
        Toast.makeText(getContext(), "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to detail screen
    }
}

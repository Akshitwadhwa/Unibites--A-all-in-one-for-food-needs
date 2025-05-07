package com.example.unibites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private static final String TAG = "HomeFragment";

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
    private ImageButton btnMenu, btnNotification;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize buttons
        btnMenu = view.findViewById(R.id.btnMenu);
        btnNotification = view.findViewById(R.id.btnNotification);

        // Set click listeners
        btnMenu.setOnClickListener(v -> {
            // Open Profile Activity
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        btnNotification.setOnClickListener(v -> {
            // TODO: Implement notification functionality
            Toast.makeText(getContext(), "Notifications clicked", Toast.LENGTH_SHORT).show();
        });

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
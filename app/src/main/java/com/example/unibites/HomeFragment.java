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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // Uncomment the line below to add sample food items
        // addSampleFoodItems();
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

    private void addSampleFoodItems() {
        // Breakfast items
        addFoodItem("Masala Dosa", 80.0, "Crispy dosa with spiced potato filling", "https://example.com/masala_dosa.jpg", "breakfast");
        addFoodItem("Idli Sambar", 60.0, "Soft idlis served with sambar", "https://example.com/idli_sambar.jpg", "breakfast");
        addFoodItem("Poha", 40.0, "Flattened rice with vegetables and spices", "https://example.com/poha.jpg", "breakfast");
        addFoodItem("Upma", 45.0, "Semolina cooked with vegetables", "https://example.com/upma.jpg", "breakfast");

        // Lunch items
        addFoodItem("Butter Chicken", 180.0, "Tender chicken in rich tomato gravy", "https://example.com/butter_chicken.jpg", "lunch");
        addFoodItem("Veg Biryani", 150.0, "Fragrant rice with mixed vegetables", "https://example.com/veg_biryani.jpg", "lunch");
        addFoodItem("Paneer Butter Masala", 160.0, "Cottage cheese in rich tomato gravy", "https://example.com/paneer_butter_masala.jpg", "lunch");
        addFoodItem("Chole Bhature", 120.0, "Spiced chickpeas with fried bread", "https://example.com/chole_bhature.jpg", "lunch");

        // Snacks items
        addFoodItem("Samosa", 30.0, "Crispy pastry with spiced potato filling", "https://example.com/samosa.jpg", "snacks");
        addFoodItem("Kachori", 25.0, "Deep-fried pastry with lentil filling", "https://example.com/kachori.jpg", "snacks");
        addFoodItem("Vada Pav", 35.0, "Spiced potato fritter in bread", "https://example.com/vada_pav.jpg", "snacks");
        addFoodItem("Pav Bhaji", 80.0, "Spiced vegetable mash with bread", "https://example.com/pav_bhaji.jpg", "snacks");

        // Dinner items
        addFoodItem("Butter Naan", 40.0, "Soft bread with butter", "https://example.com/butter_naan.jpg", "dinner");
        addFoodItem("Dal Makhani", 140.0, "Black lentils in rich gravy", "https://example.com/dal_makhani.jpg", "dinner");
        addFoodItem("Jeera Rice", 80.0, "Fragrant rice with cumin", "https://example.com/jeera_rice.jpg", "dinner");
        addFoodItem("Tandoori Roti", 30.0, "Whole wheat bread from tandoor", "https://example.com/tandoori_roti.jpg", "dinner");
    }

    private void addFoodItem(String name, double price, String description, String imageUrl, String category) {
        Map<String, Object> product = new HashMap<>();
        product.put("Productname", name);
        product.put("ProductPrice", price);
        product.put("description", description);
        product.put("Productimage", imageUrl);
        product.put("ProductCategory", category);

        db.collection("Products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Food item added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding food item", e);
                });
    }

    @Override
    public void onProductClick(Product product, int position) {
        Toast.makeText(getContext(), "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to detail screen
    }
}
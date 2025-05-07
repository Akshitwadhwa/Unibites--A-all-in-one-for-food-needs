public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.productList = products;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, price, rating;
        Button addToCart;
        LinearLayout ratingContainer;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_food_image);
            name = itemView.findViewById(R.id.tv_product_name);
            price = itemView.findViewById(R.id.tv_product_price);
            rating = itemView.findViewById(R.id.tv_rating);
            addToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_product, parent, false); // You'll create this layout
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.price.setText("₹" + product.getPrice());
        holder.rating.setText(String.valueOf(product.getRating()));
        Glide.with(context).load(product.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}

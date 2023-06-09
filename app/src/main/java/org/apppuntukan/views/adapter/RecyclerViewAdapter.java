package org.apppuntukan.views.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import android.annotation.SuppressLint;
import android.view.View;
import android.app.Activity;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import org.apppuntukan.model.ICard;
import org.apppuntukan.model.Product;
import org.apppuntukan.model.ProdServ;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import org.apppuntukan.databinding.ProductCardBinding;
import org.apppuntukan.viewmodel.CartActivityViewModel;
import org.apppuntukan.databinding.CartProductCardBinding;
import org.apppuntukan.viewmodel.ProductsActivityViewModel;

public class RecyclerViewAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerViewAdapter.CardHolder<T>> {

    private final Activity activity;
    private final int layoutId;
    private List<Product> products;
    private final RecyclerViewAdapter<T> adapter;

    public RecyclerViewAdapter(Activity activity, int layoutId, List<Product> products) {
        this.activity = activity;
        this.layoutId = layoutId;
        this.products = products;
        this.adapter = this;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.CardHolder<T> onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new CardHolder<>(DataBindingUtil
                .inflate(
                        activity.getLayoutInflater(),
                        layoutId,
                        viewGroup,
                        false
                ), adapter, products);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.CardHolder<T> holder, int position) {
        holder.bindCard(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class CardHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder implements ICard {

        private final RecyclerViewAdapter<T> adapter;
        private final List<Product> products;
        private final T binding;

        public CardHolder(T binding, RecyclerViewAdapter<T> adapter, List<Product> products) {
            super(binding.getRoot());
            this.binding = binding;
            this.adapter = adapter;
            this.products = products;
        }

        void bindCard(Product product) {
            if (binding instanceof ProductCardBinding) { // can't use pattern matching
                ((ProductCardBinding) binding).setProduct(product);
                ((ProductCardBinding) binding).setHandler(this);
            }
            if (binding instanceof CartProductCardBinding) {
                ((CartProductCardBinding) binding).setProduct(product);
                ((CartProductCardBinding) binding).setHandler(this);
            }
            binding.executePendingBindings();
        }

        @Override
        public void onClickCard(View v) {
            Toast.makeText(v.getContext(), "Card clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onIncreaseQuantity(View v) {
            int position = super.getLayoutPosition();
            ProdServ.instance().addProductQuantity(products.get(position));
            adapter.notifyItemChanged(position);
            int size = ProdServ.instance().getCartProducts().size();
            CartActivityViewModel.getInstance()
                    .updateData(new Object[]{
                            size,
                            ProdServ.instance().computeTotal()
                    });
            ProductsActivityViewModel.instance()
                    .updateData(new Object[]{size});
        }

        @Override
        public void onDecreaseQuantity(View v) { // FIXME: 02/04/2023 Cart still shows removed product.
            int position = super.getLayoutPosition();
            Product product = products.get(position);
            boolean toBeRemoved = ProdServ.instance().updateProductQuantityOrRemoveToCart(product);
            if (toBeRemoved) {
                if (products.remove(product)) {
                    ProdServ.instance().removeProductFromCart(product);
                    adapter.notifyItemRemoved(position);
                }
            } else adapter.notifyItemChanged(position);
            int size = ProdServ.instance().getCartProducts().size();
            CartActivityViewModel.getInstance()
                    .updateData(new Object[]{
                            size,
                            ProdServ.instance().computeTotal()
                    });
            ProductsActivityViewModel.instance()
                    .updateData(new Object[]{size});
        }

        @Override
        public void onClickProductImage(View v) {
            Toast.makeText(v.getContext(), "Product Image Clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAddToCart(View v) {
            int pos = super.getLayoutPosition(); // get the position of the card
            if (ProdServ.instance().isAlreadyInCart(products.get(pos)))
                ProdServ.instance().addProductQuantity(products.get(pos));
            else ProdServ.instance().addProductToCart(products.get(pos));
            Toast.makeText(v.getContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
            int size = ProdServ.instance().getCartProducts().size();
            CartActivityViewModel.getInstance()
                    .updateData(new Object[]{
                            size,
                            ProdServ.instance().computeTotal()
                    });
            ProductsActivityViewModel.instance()
                    .updateData(new Object[]{size});
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredProducts(List<Product> filteredProducts) {
        System.out.println("filteredProducts = " + filteredProducts);
        this.products = filteredProducts;
        notifyDataSetChanged();
    }

    // FIXME: 09/04/2023 nope
//    @Override
//    public Filter getFilter() {
//        List<Product> filteredList = new ArrayList<>();
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                if (constraint == null || constraint.length() == 0) {
//                    filteredList.addAll(products);
//                } else {
//                    String pattern = constraint.toString().toLowerCase().trim();
//                    for (Product product : products) {
//                        String pName = product.getProductName().toLowerCase().trim();
//                        if (pName.contains(pattern) || pName.startsWith(pattern)) {
//                            filteredList.add(product);
//                        }
//                    }
//                }
//                FilterResults results = new FilterResults();
//                results.values = filteredList;
//                return results;
//            }
//
//            @Override
//            @SuppressWarnings("unchecked")
//            @SuppressLint("NotifyDataSetChanged")
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                products.clear();
//                products.addAll((Collection<Product>) results.values);
//                System.out.println("filteredList = " + filteredList);
//                System.out.println("products = " + products);
//                System.out.println("\"ADDING ALL\" = " + "ADDING ALL");
//                notifyDataSetChanged();
//            }
//        };
//    }
}

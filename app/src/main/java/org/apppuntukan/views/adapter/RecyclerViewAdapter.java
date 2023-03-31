package org.apppuntukan.views.adapter;

import java.util.List;
import android.view.View;
import android.app.Activity;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import org.apppuntukan.model.Product;
import org.apppuntukan.viewmodel.ICard;
import org.apppuntukan.model.ProdServ;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import org.apppuntukan.databinding.ProductCardBinding;
import org.apppuntukan.databinding.CartProductCardBinding;

public class RecyclerViewAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerViewAdapter.CardHolder<T>> {

    private final Activity activity;
    private final int layoutId;
    private final List<Product> products;

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
            Snackbar.make(v, "Card clicked", Snackbar.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onIncreaseQuantity(View v) {
            int position = super.getLayoutPosition();
            ProdServ.instance().addProductQuantity(products.get(position));
            adapter.notifyItemChanged(position);
        }

        @Override
        public void onDecreaseQuantity(View v) {
            int position = super.getLayoutPosition(); // get the position of the card
            Product product = products.get(position);
            boolean noQuantityLeft = ProdServ.instance().removeProductQuantityOrRemove(product);
            if (noQuantityLeft) {
                Product removedProduct = ProdServ.instance().removeProductFromCart(product);
                if (removedProduct != null) {
                    adapter.notifyItemRemoved(position);
                }
            }
            else adapter.notifyItemChanged(position);
        }

        @Override
        public void onClickProductImage(View v) {
            Snackbar.make(v, "Product Image Clicked", Snackbar.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onAddToCart(View view) {
            int pos = super.getLayoutPosition(); // get the position of the card
            if (ProdServ.instance().isAlreadyInCart(products.get(pos))) {
                ProdServ.instance().addProductQuantity(products.get(pos));
            } else {
                ProdServ.instance().addProductToCart(products.get(pos));
            }
            Snackbar.make(view, "Product added to cart", Snackbar.LENGTH_SHORT)
                    .show();
            adapter.notifyItemChanged(pos);
        }
    }
}

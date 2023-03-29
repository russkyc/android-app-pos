package org.apppuntukan.views;

import org.apppuntukan.R;
import android.os.Bundle;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import org.apppuntukan.viewmodel.ICard;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.GridLayoutManager;
import org.apppuntukan.databinding.ActivityMainBinding;
import org.apppuntukan.views.adapter.MainCustomerAdapter;
import org.apppuntukan.viewmodel.ProductsActivityViewModel;
import org.apppuntukan.model.abstractions.NoActionBarActivity;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

public class MainActivity extends NoActionBarActivity implements ICard {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setProductsViewModel(new ViewModelProvider(this).get(ProductsActivityViewModel.class));

        setContentView(binding.getRoot());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new MainCustomerAdapter(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    @Override
    public void onClickCard(View v) { // FIXME: 28/03/2023 Won't work
        Snackbar.make(v, "Card clicked", Snackbar.LENGTH_SHORT)
                .show();
        System.out.println("Clicked card");
    }

    @Override
    public void onAddToCart(View v) { // FIXME: 28/03/2023 Won't work
        Snackbar.make(v, "Added to cart", Snackbar.LENGTH_SHORT)
                .show();
        System.out.println("Add to Cart clicked");
    }

}

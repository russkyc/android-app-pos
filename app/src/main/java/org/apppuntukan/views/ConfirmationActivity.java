package org.apppuntukan.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import org.apppuntukan.R;
import org.apppuntukan.databinding.ActivityConfirmationBinding;
import org.apppuntukan.model.Product;
import org.apppuntukan.model.ProdServ;
import org.apppuntukan.viewmodel.ConfirmationActivityViewModel;
import java.util.List;

public class ConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityConfirmationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_confirmation);
        binding.setConfirmationViewModel(ConfirmationActivityViewModel.instance());
        binding.setLifecycleOwner(this);
        setContentView(binding.getRoot());

        List<Product> products = ProdServ.instance().getProducts();
        List<Product> cartProducts = ProdServ.instance().getCartProducts();

        for (int i = 0; i < cartProducts.size(); i++) {
            Product product = cartProducts.get(i);
            if (products.get(i).getId() == product.getId()) { // FIXME: 25/03/2023 decrease product stock based on ordered quantity
                if (product.getStock() >= 0) product.setStock(product.getStock() - product.getQuantity());
                products.get(i).setStock(product.getStock());
            }
        }
    }
}
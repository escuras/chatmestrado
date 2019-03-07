package pt.IPG.messenger;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.IPG.messenger.recyclerview.Contact;
import pt.IPG.messenger.recyclerview.ContactAdapter;
import pt.IPG.messenger.recyclerview.Product;
import pt.IPG.messenger.recyclerview.ProductAdapter;

public class FragmentProducts extends Fragment implements ProductAdapter.ViewHolder.ClickListener {

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private MainActivity activity;
    private View view;

    public FragmentProducts() {
        setHasOptionsMenu(true);
    }

    public void onCreate(Bundle a) {
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products, null, false);
        activity = ((MainActivity) getActivity());
        activity.changeTitle(R.id.toolbar, "Products");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ProductAdapter(getContext(), setData(), this);
        mRecyclerView.setAdapter(mAdapter);
        addListenerToPayButton(view);
        return view;
    }

    private void addListenerToPayButton(View view){
        Button button = (Button) view.findViewById(R.id.button_pay_products);
        System.out.println(button);
        if(button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FragmentPayProducts fragmentPayProducts = new FragmentPayProducts();
                    Bundle args = new Bundle();
                    args.putSerializable("products", (Serializable) mAdapter.getSelected());
                    fragmentPayProducts.setArguments(args);
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout, fragmentPayProducts).commit();
                }
            });
        }
    }

    public List<Product> setData() {

        List<Product> data = new ArrayList<>();

        String[] names = {"Samsung S10", "Iphone XS Max", "Huwaei P20", "Xiaomi MI 9", "Lenovo Z5 Pro","Xiaomi Mi Mix 3"};
        @DrawableRes int[] imgs = {R.drawable.product1, R.drawable.product2, R.drawable.product3, R.drawable.product4, R.drawable.product5, R.drawable.product6};
        String[] brands = {"Samsung", "Apple", "Huwaei", "Xiaomi", "Lenovo", "Xiaomi"};
        String[] descriptions = {"Samsung phone", "Apple phone", "Huwaei phone", "Xiaomi phone", "Lenovo phone", "Xiaomi phone"};
        double[] prices = {919.90, 1269.90, 439.99, 667.50, 450.00, 559.99};
        for (int i = 0; i < names.length; i++) {
            Product product = new Product();
            product.setName(names[i]);
            product.setImage(imgs[i]);
            product.setBrand(brands[i]);
            product.setPrice(prices[i]);
            product.setDescription(descriptions[i]);
            data.add(product);
        }
        return data;
    }

    @Override
    public void onItemClicked(int position) {
        toggleSelection(position);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_back:
                FragmentContacts fragmentContacts = new FragmentContacts();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentContacts).commit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_back, menu);
    }
}

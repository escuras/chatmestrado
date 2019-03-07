package pt.IPG.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pt.IPG.messenger.recyclerpayments.GooglePaymentVerify;
import pt.IPG.messenger.recyclerview.Product;

public class FragmentPayProducts extends FragmentPay {

    private static final int RC_SIGN_IN = 9001;
    private double value = 0.0;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;
    private static String MERCHANT_ID = "merchantIdExample";


    @Override
    public void onCreate(Bundle a){
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_products, null, false);
        Bundle bundle = getArguments();
        List<Product> products = (List<Product>) bundle.getSerializable("products");
        if(products == null) {
            products = new ArrayList<>();
        }
        activityHome = ((MainActivity) getActivity());
        signGoogleAccount();
        customize(products, view);
        preparePaymentScenario();
        return view;
    }

    protected void preparePaymentScenario(){
        Wallet.WalletOptions.Builder builder = new Wallet.WalletOptions.Builder();
        Wallet.WalletOptions options = builder.setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        mPaymentsClient = Wallet.getPaymentsClient(activityHome, options);
        possiblyShowGooglePayButton();
    }


    private void customize(List<Product> products, View view){
        TextView text = (TextView) view.findViewById(R.id.textViewPayProduct);
        for(Product pr : products) {
            value += pr.getPrice();
        }
        text.setText(getResources().getString(R.string.value_to_pay) + ": " + value + "€");
        mGooglePayButton = view.findViewById(R.id.buttonPaymentsProducts);
        if(value == 0.0) {
            mGooglePayButton.setEnabled(false);
        } else {
            mGooglePayButton.setEnabled(true);
        }
    }

    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = GooglePaymentVerify.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            if (result) {
                                // show Google as a payment option
                                mGooglePayButton = activityHome.findViewById(R.id.buttonPaymentsProducts);
                                mGooglePayButton.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Switch switchd = (Switch) activityHome.findViewById(R.id.switchPayProducts);
                                                if(switchd.isChecked()) {
                                                    EditText mEdit   = (EditText) activityHome.findViewById(R.id.editPayment);
                                                    requestPayment(view, MERCHANT_ID, String.valueOf(value), LOAD_PAYMENT_DATA_REQUEST_CODE);
                                                    mGooglePayButton.setEnabled(false);
                                                }
                                            }
                                        });
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            }
                        } catch (ApiException exception) {
                            Log.w("FragmentPayProducts", exception.getMessage());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_back:
                FragmentProducts fragmentProducts = new FragmentProducts();
                FragmentTransaction ft = activityHome.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentProducts).commit();
                break;
            default:
                break;
        }
        return true;
    }
}

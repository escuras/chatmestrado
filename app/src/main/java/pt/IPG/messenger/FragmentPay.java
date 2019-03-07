package pt.IPG.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONObject;

import java.util.Optional;

import pt.IPG.messenger.recyclerpayments.GooglePaymentRequest;
import pt.IPG.messenger.recyclerpayments.GooglePaymentVerify;
import pt.IPG.messenger.recyclerview.Contact;

public class FragmentPay extends Fragment {

    protected MainActivity activityHome;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Contact contact;
    protected GoogleSignInClient mGoogleSignInClient;
    protected View mGooglePayButton;
    protected GoogleSignInAccount account;
    private static final int RC_SIGN_IN = 9001;
    protected PaymentsClient mPaymentsClient;
    private static final int LOAD_TRANSFER_DATA_REQUEST_CODE = 41;
    private static int ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;


    @Override
    public void onCreate(Bundle a){
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, null, false);
        Bundle bundle = getArguments();
        contact = (Contact) bundle.getSerializable("contact");
        activityHome = ((MainActivity) getActivity());
        customize(contact, view);
        signGoogleAccount();
        preparePaymentScenario();
        return view;
    }

    protected void signGoogleAccount(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(activityHome, gso);
        account = GoogleSignIn.getLastSignedInAccount(activityHome);
        if (account == null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    protected void preparePaymentScenario(){
        Wallet.WalletOptions.Builder builder = new Wallet.WalletOptions.Builder();
        Wallet.WalletOptions options = builder.setEnvironment(ENVIRONMENT).build();
        mPaymentsClient = Wallet.getPaymentsClient(activityHome, options);
        prepareButton();
    }

    private void prepareButton() {
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
                                mGooglePayButton = activityHome.findViewById(R.id.buttonPayments);
                                mGooglePayButton.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Switch switchd = (Switch) activityHome.findViewById(R.id.switchPayments);
                                                if(switchd.isChecked()) {
                                                    EditText mEdit   = (EditText) activityHome.findViewById(R.id.editPayment);
                                                    try {
                                                        double value = Double.valueOf(mEdit.getText().toString());
                                                        if (value > 0) {
                                                            requestPayment(view, contact.getEmail(), String.valueOf(value), LOAD_TRANSFER_DATA_REQUEST_CODE);
                                                            mGooglePayButton.setEnabled(false);
                                                        }
                                                    } catch(NumberFormatException ex) {
                                                        Toast.makeText(activityHome, "Value is not a number", Toast.LENGTH_SHORT);
                                                    }
                                                }
                                            }
                                        });
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            }
                        } catch (ApiException exception) {
                            Log.w("FragmentPay", exception.getMessage());
                        }
                    }
                });
    }

    protected void requestPayment(View view, String merchantName, String value, int code) {
        GooglePaymentRequest payment = new GooglePaymentRequest(getResources().getString(R.string.public_key), merchantName, value);
        Optional<JSONObject> paymentDataRequestJson = payment.getPaymentDataRequest();
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request = PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());
        if (request != null) {
            activityHome.callAutoResolveHelper(mPaymentsClient, request, code);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                System.out.println(task);
                handleSignInResult(task);
                break;
            default:
                break;

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
        if(account == null) {
            Toast.makeText(activityHome, "Error when trying to sign in a google account", Toast.LENGTH_SHORT);
            if(this instanceof FragmentPay) {
                FragmentPayments fragmentPayments = new FragmentPayments();
                FragmentTransaction ft = activityHome.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentPayments).commit();
            } else if (this instanceof  FragmentPayProducts) {
                FragmentPayProducts fragmentPayProducts = new FragmentPayProducts();
                FragmentTransaction ft = activityHome.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentPayProducts).commit();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_back, menu);
    }

    private void customize(Contact contact, View view){
        TextView text = (TextView) view.findViewById(R.id.textViewPaymentsUser);
        text.setText(getResources().getString(R.string.transfer_to) + ": " + contact.getName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_back:
                FragmentPayments fragmentPayments = new FragmentPayments();
                FragmentTransaction ft = activityHome.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentPayments).commit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

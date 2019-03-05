package pt.IPG.messenger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONObject;

import java.util.Optional;

import pt.IPG.messenger.recyclerpayments.PaymentRequest;
import pt.IPG.messenger.recyclerpayments.PaymentVerify;
import pt.IPG.messenger.recyclerview.Contact;

public class FragmentPay extends Fragment {

    private RecyclerView mRecyclerView;
    private MainActivity activityHome;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Contact contact;
    private GoogleSignInClient mGoogleSignInClient;
    private View mGooglePayButton;
    private GoogleSignInAccount account;
    private static final int RC_SIGN_IN = 9001;
    private PaymentsClient mPaymentsClient;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;


    @Override
    public void onCreate(Bundle a){
        super.onCreate(a);
        setHasOptionsMenu(true);
    }
    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = PaymentVerify.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        System.out.println("task inform: " + task.toString());
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
                                                EditText mEdit   = (EditText) activityHome.findViewById(R.id.editPayment);
                                                String valor = mEdit.getText().toString();
                                                System.out.println(valor);
                                                System.out.println(contact.getEmail());
                                                requestPayment(view, contact.getEmail(), valor);
                                                mGooglePayButton.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            }
                        } catch (ApiException exception) {
                            // handle developer errorsVI
                        }
                    }
                });
    }

    public void requestPayment(View view, String merchantName, String value) {
        PaymentRequest payment = new PaymentRequest(getResources().getString(R.string.public_key), merchantName, value);
        Optional<JSONObject> paymentDataRequestJson = payment.getPaymentDataRequest();
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), activityHome, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_payments, null, false);
        Bundle bundle = getArguments();
        contact = (Contact) bundle.getSerializable("contact");
        activityHome = ((MainActivity) getActivity());

        customize(contact, view);
        SharedPreferences sharedPref = activityHome.getSharedPreferences("myPrefs", 0);
        String val = sharedPref.getString("email", "");
        signGoogleAccount();
        preparePaymentScenario();
        return view;
    }

    private void signGoogleAccount(){
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

    private void preparePaymentScenario(){
        Wallet.WalletOptions.Builder builder = new Wallet.WalletOptions.Builder();
        Wallet.WalletOptions options = builder.setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        mPaymentsClient = Wallet.getPaymentsClient(activityHome, options);
        possiblyShowGooglePayButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("resultCode" + resultCode);
        System.out.println("requestCode" + resultCode);
        switch (requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                System.out.println(task);
                handleSignInResult(task);
                break;
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                System.out.println(resultCode);
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        String json = paymentData.toJson();
                        System.out.println(json);
                        break;
                    case Activity.RESULT_CANCELED:
                        System.out.println("fail");
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        Log.w("PaymentStatus", status.getStatusMessage());
                        break;
                    default:
                }
                break;
            default:
                break;

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
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

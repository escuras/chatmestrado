package pt.IPG.messenger.recyclerpayments;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class PaymentRequest {

    private static String[] currencies = {"EUR", "USD"};
    private String pubKey;
    private String totalPrice;
    private String currency = currencies[0];
    private String merchantName;

    public PaymentRequest(String pubKey, String merchantName, String totalPrice) {
        this.pubKey = pubKey;
        this.merchantName = merchantName;
        this.totalPrice = totalPrice;
    }

    public void setCurrency(int val){
        switch(val) {
            case 1:
                currency = currencies[1];
                break;
            default:
                currency = currencies[0];
                break;
        }
    }

    public String getCurrency(){
        return currency;
    }

    public void setMerchantName(String merchantName){
        this.merchantName = merchantName;
    }

    public String getMerchantName(){
        return merchantName;
    }

    public void setTotalPrice(String totalPrice){
        this.totalPrice = totalPrice;
    }

    public String getTotalPrice(){
        return totalPrice;
    }

    private JSONObject getTransactionInfo() {
        JSONObject transactionInfo = new JSONObject();
        try {
            transactionInfo.put("totalPrice", totalPrice);
            transactionInfo.put("totalPriceStatus", "FINAL");
            transactionInfo.put("currencyCode", currency);
        } catch (JSONException ex) {
            Log.w("PaymentRequest", ex.getMessage());
        }
        return transactionInfo;
    }

    private JSONObject getMerchantInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("merchantName", merchantName);
        } catch (JSONException ex) {
            Log.w("PaymentRequest", ex.getMessage());
        }
        return jsonObject;
    }

    public Optional<JSONObject> getPaymentDataRequest() {
        JSONObject paymentDataRequest = PaymentUtils.getBaseRequest();
        try {
            paymentDataRequest.put(
                    "allowedPaymentMethods",
                    new JSONArray()
                            .put(PaymentUtils.getCardPaymentMethod(pubKey)));
            paymentDataRequest.put("transactionInfo", getTransactionInfo());
            paymentDataRequest.put("merchantInfo", getMerchantInfo());
            return Optional.of(paymentDataRequest);
        } catch(JSONException ex) {
            Log.w("PaymentRequest", ex.getMessage());
        }
        return Optional.empty();
    }
}

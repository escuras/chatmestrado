package pt.IPG.messenger.recyclerpayments;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GooglePaymentUtils {

    public static JSONObject getBaseRequest() {
        JSONObject apiVersion = new JSONObject();
        try {
            apiVersion.put("apiVersion", 2);
            apiVersion.put("apiVersionMinor", 0);
        } catch (JSONException ex){
            Log.w("GooglePaymentUtils", ex.getMessage());
        }
        return apiVersion;
    }

    private static JSONObject getTokenizationSpecification(String pubKey) {
        JSONObject tokenizationSpecification = new JSONObject();
        try {
            tokenizationSpecification.put("type", "DIRECT");
            tokenizationSpecification.put(
                    "parameters",
                    new JSONObject()
                            .put("protocolVersion", "ECv1")
                            .put("publicKey", pubKey));
        } catch (JSONException ex) {
            Log.w("GooglePaymentUtils", ex.getMessage());
        }
        return tokenizationSpecification;
    }

    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA");
    }

    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    public static JSONObject getBaseCardPaymentMethod() {
        JSONObject cardPaymentMethod = new JSONObject();
        try {
            cardPaymentMethod.put("type", "CARD");
            cardPaymentMethod.put(
                    "parameters",
                    new JSONObject()
                            .put("allowedAuthMethods", getAllowedCardAuthMethods())
                            .put("allowedCardNetworks", getAllowedCardNetworks()));
        } catch (JSONException ex) {
            Log.w("GooglePaymentUtils", ex.getMessage());
        }
        return cardPaymentMethod;
    }

    public static JSONObject getCardPaymentMethod(String pubKey) {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        try {
            cardPaymentMethod.put("tokenizationSpecification", getTokenizationSpecification(pubKey));
        } catch (JSONException ex) {
            Log.w("GooglePaymentUtils", ex.getMessage());
        }
        return cardPaymentMethod;
    }


}

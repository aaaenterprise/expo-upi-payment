package com.expoupi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import java.util.List;

public class ExpoUPIPaymentModule extends ReactContextBaseJavaModule {
    private static final String TAG = "ExpoUPIPayment";
    private static final int UPI_PAYMENT_REQUEST = 1001;
    
    private Promise mPromise;
    private String mTransactionRef;
    
    public ExpoUPIPaymentModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(new BaseActivityEventListener() {
            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                if (requestCode == UPI_PAYMENT_REQUEST) {
                    handleUPIResponse(resultCode, data);
                }
            }
        });
    }
    
    @Override
    public String getName() {
        return "ExpoUPIPayment";
    }
    
    @ReactMethod
    public void getInstalledUPIApps(Promise promise) {
        try {
            Activity currentActivity = getCurrentActivity();
            if (currentActivity == null) {
                promise.reject("ERR_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist");
                return;
            }
            
            Uri uri = Uri.parse("upi://pay");
            Intent upiPayIntent = new Intent(Intent.ACTION_VIEW, uri);
            PackageManager pm = currentActivity.getPackageManager();
            List<ResolveInfo> apps = pm.queryIntentActivities(upiPayIntent, 0);
            
            WritableArray upiApps = new WritableNativeArray();
            for (ResolveInfo app : apps) {
                WritableMap appInfo = new WritableNativeMap();
                appInfo.putString("packageName", app.activityInfo.packageName);
                appInfo.putString("appName", app.loadLabel(pm).toString());
                // You can also load and convert the app icon if needed
                upiApps.pushMap(appInfo);
            }
            
            promise.resolve(upiApps);
        } catch (Exception e) {
            promise.reject("ERR_UNEXPECTED_ERROR", e.getMessage());
        }
    }
    
    @ReactMethod
    public void initiatePayment(ReadableMap params, Promise promise) {
        try {
            Activity currentActivity = getCurrentActivity();
            if (currentActivity == null) {
                promise.reject("ERR_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist");
                return;
            }
            
            mPromise = promise;
            mTransactionRef = params.getString("transactionRef");
            
            String vpa = params.getString("vpa");
            String name = params.getString("name");
            String amount = params.getString("amount");
            String transactionNote = params.hasKey("transactionNote") ? params.getString("transactionNote") : "";
            String currency = params.hasKey("currency") ? params.getString("currency") : "INR";
            
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", vpa)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", currency)
                .appendQueryParameter("tr", mTransactionRef);
            
            if (!transactionNote.isEmpty()) {
                uriBuilder.appendQueryParameter("tn", transactionNote);
            }
            
            Uri uri = uriBuilder.build();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            
            currentActivity.startActivityForResult(intent, UPI_PAYMENT_REQUEST);
        } catch (Exception e) {
            if (promise != null) {
                WritableMap result = createErrorResponse(mTransactionRef, e.getMessage());
                promise.resolve(result);
                mPromise = null;
            }
        }
    }
    
    @ReactMethod
    public void initiatePaymentWithApp(ReadableMap params, String packageName, Promise promise) {
        try {
            Activity currentActivity = getCurrentActivity();
            if (currentActivity == null) {
                promise.reject("ERR_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist");
                return;
            }
            
            mPromise = promise;
            mTransactionRef = params.getString("transactionRef");
            
            String vpa = params.getString("vpa");
            String name = params.getString("name");
            String amount = params.getString("amount");
            String transactionNote = params.hasKey("transactionNote") ? params.getString("transactionNote") : "";
            String currency = params.hasKey("currency") ? params.getString("currency") : "INR";
            
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", vpa)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", currency)
                .appendQueryParameter("tr", mTransactionRef);
            
            if (!transactionNote.isEmpty()) {
                uriBuilder.appendQueryParameter("tn", transactionNote);
            }
            
            Uri uri = uriBuilder.build();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(packageName);
            
            currentActivity.startActivityForResult(intent, UPI_PAYMENT_REQUEST);
        } catch (Exception e) {
            if (promise != null) {
                WritableMap result = createErrorResponse(mTransactionRef, e.getMessage());
                promise.resolve(result);
                mPromise = null;
            }
        }
    }
    
    private void handleUPIResponse(int resultCode, Intent data) {
        if (mPromise == null) {
            return;
        }
        
        try {
            WritableMap result = new WritableNativeMap();
            
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        Log.d(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                    }
                }
                
                // Different UPI apps may return status in different keys
                String status = data.getStringExtra("Status");
                if (status == null) {
                    status = data.getStringExtra("status");
                }
                if (status == null) {
                    status = data.getStringExtra("response");
                }
                
                String responseCode = data.getStringExtra("responseCode");
                String approvalRefNo = data.getStringExtra("ApprovalRefNo");
                String transactionId = data.getStringExtra("txnId");
                
                result.putString("transactionRef", mTransactionRef);
                
                if (status != null) {
                    result.putString("rawResponse", status);
                    
                    status = status.toLowerCase();
                    if (status.contains("success")) {
                        result.putString("status", "SUCCESS");
                    } else if (status.contains("fail")) {
                        result.putString("status", "FAILURE");
                    } else if (status.contains("submitted")) {
                        result.putString("status", "SUBMITTED");
                    } else if (status.contains("cancel")) {
                        result.putString("status", "CANCELLED");
                    } else {
                        result.putString("status", "UNKNOWN");
                    }
                } else {
                    result.putString("status", "UNKNOWN");
                }
                
                if (responseCode != null) {
                    result.putString("responseCode", responseCode);
                }
                
                if (approvalRefNo != null) {
                    result.putString("approvalRefNo", approvalRefNo);
                }
                
                if (transactionId != null) {
                    result.putString("transactionId", transactionId);
                }
            } else {
                // User likely canceled or payment failed
                if (resultCode == Activity.RESULT_CANCELED) {
                    result.putString("status", "CANCELLED");
                } else {
                    result.putString("status", "FAILURE");
                }
                result.putString("transactionRef", mTransactionRef);
            }
            
            mPromise.resolve(result);
        } catch (Exception e) {
            mPromise.reject("ERR_UNEXPECTED_ERROR", e.getMessage());
        } finally {
            mPromise = null;
        }
    }
    
    private WritableMap createErrorResponse(String transactionRef, String errorMessage) {
        WritableMap result = new WritableNativeMap();
        result.putString("status", "FAILURE");
        result.putString("transactionRef", transactionRef);
        result.putString("responseCode", "ERR_UNEXPECTED_ERROR");
        result.putString("rawResponse", errorMessage);
        return result;
    }
}
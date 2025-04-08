# Expo UPI Payment

A React Native Expo library for integrating UPI payments in Android apps.

## Features

- Open UPI payment apps using deep linking
- Show all available UPI apps installed on the device
- Track payment status (success/failure/cancelled)
- Works entirely on the client side (no backend required)
- Android only (UPI is not available on iOS)

## Installation

```bash
npm install expo-upi-payment
# or
yarn add expo-upi-payment
```

### Expo Configuration

This library requires custom native code, so you'll need to use either:
- Expo Development Builds
- EAS Build
- Expo prebuild workflow

Add the config plugin to your app.json/app.config.js:

```json
{
  "expo": {
    "plugins": [
      "expo-upi-payment"
    ]
  }
}
```

Then rebuild your app:

```bash
npx expo prebuild --clean
# or
eas build
```

## Usage

### 1. Get installed UPI apps

```javascript
import UPIPayment from 'expo-upi-payment';

// Get list of UPI apps installed on the device
const getUPIApps = async () => {
  try {
    const apps = await UPIPayment.getInstalledUPIApps();
    console.log('Installed UPI apps:', apps);
    /*
    [
      { packageName: 'net.one97.paytm', appName: 'Paytm' },
      { packageName: 'com.phonepe.app', appName: 'PhonePe' },
      ...
    ]
    */
  } catch (error) {
    console.error('Error getting UPI apps:', error);
  }
};
```

### 2. Initiate payment (showing all UPI apps)

```javascript
const makePayment = async () => {
  try {
    const paymentParams = {
      vpa: 'merchant@upi',          // Payee UPI ID
      name: 'Merchant Name',        // Payee name
      amount: '100.00',             // Amount as string
      transactionRef: 'TXN123456',  // Unique reference ID
      transactionNote: 'Payment for order #123', // Optional
      currency: 'INR'               // Optional (default: INR)
    };
    
    const result = await UPIPayment.initiatePayment(paymentParams);
    console.log('Payment result:', result);
    /*
    {
      status: 'SUCCESS', // or 'FAILURE', 'CANCELLED', 'SUBMITTED', 'UNKNOWN'
      transactionRef: 'TXN123456',
      transactionId: '...', // if available
      responseCode: '...',  // if available
      approvalRefNo: '...', // if available
      rawResponse: '...'    // raw response string
    }
    */
  } catch (error) {
    console.error('Payment error:', error);
  }
};
```

### 3. Initiate payment with a specific UPI app

```javascript
const makePaymentWithApp = async (appPackage) => {
  try {
    const paymentParams = {
      vpa: 'merchant@upi',
      name: 'Merchant Name',
      amount: '100.00',
      transactionRef: 'TXN123456',
      transactionNote: 'Payment for order #123',
    };
    
    // Example: Pay with Google Pay
    const result = await UPIPayment.initiatePaymentWithApp(
      paymentParams,
      'com.google.android.apps.nbu.paisa.user'
    );
    console.log('Payment result:', result);
  } catch (error) {
    console.error('Payment error:', error);
  }
};
```

## How it works

1. The library uses Android's Intent system to open UPI payment URLs
2. When a payment is initiated, the user is taken to the UPI app
3. After completing or cancelling the payment, the user returns to your app
4. The library captures the result using Android's `onActivityResult` mechanism
5. The promise resolves with the payment status and details

## Payment Status Values

- `SUCCESS`: Payment was successful
- `FAILURE`: Payment failed
- `CANCELLED`: User cancelled the payment
- `SUBMITTED`: Payment request was submitted, but final status is unknown
- `UNKNOWN`: Unable to determine payment status

## Notes

- This library works only on Android devices with UPI apps installed
- No backend communication is needed for basic payment flow
- For production use, it's recommended to verify payments with your backend
- The UPI schemes may vary across different UPI apps, this library handles common patterns

## License

MIT
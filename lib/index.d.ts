import type { UPIAppInfo, UPIPaymentParams, PaymentResult } from './types';
/**
 * UPI Payment library for React Native Expo (Android only)
 */
declare const UPIPayment: {
    /**
     * Get list of installed UPI apps on the device
     * @returns Promise resolving to an array of UPI app information
     */
    getInstalledUPIApps: () => Promise<UPIAppInfo[]>;
    /**
     * Initiate UPI payment and show all available UPI apps
     * @param params Payment parameters
     * @returns Promise resolving to payment result
     */
    initiatePayment: (params: UPIPaymentParams) => Promise<PaymentResult>;
    /**
     * Initiate UPI payment with a specific app
     * @param params Payment parameters
     * @param appPackage Package name of the UPI app
     * @returns Promise resolving to payment result
     */
    initiatePaymentWithApp: (params: UPIPaymentParams, appPackage: string) => Promise<PaymentResult>;
};
export default UPIPayment;

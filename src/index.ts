import { NativeModules, Platform } from 'react-native';
import type { UPIAppInfo, UPIPaymentParams, PaymentResult } from './types';

const { ExpoUPIPayment } = NativeModules;

/**
 * UPI Payment library for React Native Expo (Android only)
 */
const UPIPayment = {
  /**
   * Get list of installed UPI apps on the device
   * @returns Promise resolving to an array of UPI app information
   */
  getInstalledUPIApps: async (): Promise<UPIAppInfo[]> => {
    if (Platform.OS !== 'android') {
      console.warn('UPI Payment is only supported on Android');
      return [];
    }
    
    try {
      return await ExpoUPIPayment.getInstalledUPIApps();
    } catch (error) {
      console.error('Error getting installed UPI apps:', error);
      return [];
    }
  },
  
  /**
   * Initiate UPI payment and show all available UPI apps
   * @param params Payment parameters
   * @returns Promise resolving to payment result
   */
  initiatePayment: async (params: UPIPaymentParams): Promise<PaymentResult> => {
    if (Platform.OS !== 'android') {
      console.warn('UPI Payment is only supported on Android');
      return { 
        status: 'UNKNOWN', 
        transactionRef: params.transactionRef 
      };
    }
    
    try {
      return await ExpoUPIPayment.initiatePayment(params);
    } catch (error) {
      console.error('Error initiating UPI payment:', error);
      return {
        status: 'FAILURE',
        transactionRef: params.transactionRef,
        responseCode: 'UNKNOWN_ERROR',
        rawResponse: String(error)
      };
    }
  },
  
  /**
   * Initiate UPI payment with a specific app
   * @param params Payment parameters
   * @param appPackage Package name of the UPI app
   * @returns Promise resolving to payment result
   */
  initiatePaymentWithApp: async (
    params: UPIPaymentParams,
    appPackage: string
  ): Promise<PaymentResult> => {
    if (Platform.OS !== 'android') {
      console.warn('UPI Payment is only supported on Android');
      return { 
        status: 'UNKNOWN', 
        transactionRef: params.transactionRef 
      };
    }
    
    try {
      return await ExpoUPIPayment.initiatePaymentWithApp(params, appPackage);
    } catch (error) {
      console.error('Error initiating UPI payment:', error);
      return {
        status: 'FAILURE',
        transactionRef: params.transactionRef,
        responseCode: 'UNKNOWN_ERROR',
        rawResponse: String(error)
      };
    }
  }
};

export default UPIPayment;
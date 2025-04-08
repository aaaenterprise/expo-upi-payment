/**
 * Information about a UPI app installed on the device
 */
export type UPIAppInfo = {
    /** Package name of the UPI app */
    packageName: string;
    /** Display name of the UPI app */
    appName: string;
    /** Optional base64 encoded icon of the app */
    icon?: string;
};
/**
 * Parameters for initiating a UPI payment
 */
export type UPIPaymentParams = {
    /** Virtual Payment Address (UPI ID) of the recipient */
    vpa: string;
    /** Name of the recipient */
    name: string;
    /** Amount to pay (as string) */
    amount: string;
    /** Unique transaction reference ID */
    transactionRef: string;
    /** Optional note for the transaction */
    transactionNote?: string;
    /** Currency code (default: INR) */
    currency?: string;
};
/**
 * Possible payment status values
 */
export type PaymentStatus = 'SUCCESS' | 'FAILURE' | 'CANCELLED' | 'SUBMITTED' | 'UNKNOWN';
/**
 * Result of a UPI payment operation
 */
export type PaymentResult = {
    /** Status of the payment */
    status: PaymentStatus;
    /** Transaction reference ID */
    transactionRef: string;
    /** UPI transaction ID (if available) */
    transactionId?: string;
    /** Response code from the UPI app */
    responseCode?: string;
    /** Approval reference number */
    approvalRefNo?: string;
    /** Raw response from the UPI app */
    rawResponse?: string;
};

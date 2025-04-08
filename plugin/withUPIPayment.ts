import { ConfigPlugin, createRunOncePlugin, withPlugins } from '@expo/config-plugins';

/**
 * Config plugin for adding UPI Payment support to Expo projects
 */
const withUPIPayment: ConfigPlugin = (config) => {
  return withPlugins(config, [
    // Add Android permissions
    (config) => {
      if (!config.android) config.android = {};
      if (!config.android.permissions) config.android.permissions = [];
      
      // No special permissions required for UPI payment links
      return config;
    },
    
    // Configure Android project (can be extended for more complex configurations)
    (config) => {
      return config;
    },
  ]);
};

export default createRunOncePlugin(
  withUPIPayment,
  'expo-upi-payment',
  '0.1.0'
);
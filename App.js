/** @format */

import * as React from 'react';
import {Component} from 'react';
import {Provider} from 'react-redux';
import {persistStore} from 'redux-persist';
import {PersistGate} from 'redux-persist/es/integration/react';
import OneSignal from 'react-native-onesignal';
import {enableScreens} from 'react-native-screens';
import 'react-native-gesture-handler';

import {Config} from '@common';
import {getNotification} from '@app/Omni';
import store from '@store/configureStore';

import Router from './src/Router';

enableScreens();

export default class ReduxWrapper extends Component {
  state = {
    requiresPrivacyConsent: false,
    isSubscribed: false
  };

  async componentDidMount() {
    try {
      const notification = await getNotification();

      if (notification && Config.OneSignal && Config.OneSignal.appId) {
        OneSignal.setAppId(Config.OneSignal.appId);

        OneSignal.setLogLevel(6, 0);

        // Đặt giá trị mặc định cho requiresPrivacyConsent
        OneSignal.setRequiresUserPrivacyConsent(false);

        /* O N E S I G N A L  H A N D L E R S */
        OneSignal.setNotificationWillShowInForegroundHandler(
          notifReceivedEvent => {
            this.OSLog(
              'OneSignal: notification will show in foreground:',
              notifReceivedEvent,
            );
          },
        );
        OneSignal.setNotificationOpenedHandler(notification => {
          this.OSLog('OneSignal: notification opened:', notification);
        });
        OneSignal.setInAppMessageClickHandler(event => {
          this.OSLog('OneSignal IAM clicked:', event);
        });
        OneSignal.setInAppMessageLifecycleHandler({
          onWillDisplayInAppMessage: message => {
            this.OSLog('OneSignal: will display IAM: ', message.messageId);
          },
          onDidDisplayInAppMessage: message => {
            this.OSLog('OneSignal: did display IAM: ', message.messageId);
          },
          onWillDismissInAppMessage: message => {
            this.OSLog('OneSignal: will dismiss IAM: ', message.messageId);
          },
          onDidDismissInAppMessage: message => {
            this.OSLog('OneSignal: did dismiss IAM: ', message.messageId);
          },
        });
        OneSignal.addEmailSubscriptionObserver(event => {
          this.OSLog('OneSignal: email subscription changed: ', event);
        });
        OneSignal.addSMSSubscriptionObserver(event => {
          this.OSLog('OneSignal: SMS subscription changed: ', event);
        });
        OneSignal.addSubscriptionObserver(event => {
          this.OSLog('OneSignal: subscription changed:', event);
          this.setState({isSubscribed: event.to.isSubscribed});
        });
        OneSignal.addPermissionObserver(event => {
          this.OSLog('OneSignal: permission changed:', event);
        });
      }
    } catch (error) {
      console.log('OneSignal setup error:', error);
    }
  }

  componentWillUnmount() {
    OneSignal.clearHandlers();
  }

  OSLog = (message, optionalArg) => {
    if (optionalArg) {
      message = message + JSON.stringify(optionalArg);
    }

    console.log(message);
  };

  render() {
    const persistor = persistStore(store);

    return (
      <Provider store={store}>
        <PersistGate loading={null} persistor={persistor}>
          <Router />
        </PersistGate>
      </Provider>
    );
  }
}

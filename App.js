/** @format */

import * as React from 'react';
import {Component} from 'react';
import {Provider} from 'react-redux';
import {persistStore} from 'redux-persist';
import {PersistGate} from 'redux-persist/es/integration/react';
import {enableScreens} from 'react-native-screens';
import 'react-native-gesture-handler';

import {Config} from '@common';
import store from '@store/configureStore';

import Router from './src/Router';

enableScreens();

export default class ReduxWrapper extends Component {
  async componentDidMount() {
    // Đã loại bỏ các cấu hình OneSignal
  }

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

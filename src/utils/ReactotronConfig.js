/** @format */

import reactotron from 'reactotron-react-native';

const _log = values => __DEV__ && reactotron.log(values);
const _warn = values => __DEV__ && reactotron.warn(values);
const _error = values => __DEV__ && reactotron.error(values);

export function connectConsoleToReactotron() {
  // console.log = _log;
  // console.warn = _warn;
  // console.error = _error;
}

export const log = _log;
export const warn = _warn;
export const error = _error;
export const Reactotron = reactotron; 
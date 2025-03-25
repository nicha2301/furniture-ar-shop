import {LoginManager, AccessToken} from 'react-native-fbsdk-next';

export default class Facebook {
  static logInWithReadPermissionsAsync(logInID, options) {
    return LoginManager.logInWithPermissions(options.permissions).then(
      result => {
        if (result.isCancelled) {
          return;
        }
        return AccessToken.getCurrentAccessToken().then(data => {
          return {type: 'success', token: data.accessToken};
        });
      },
    );
  }
}

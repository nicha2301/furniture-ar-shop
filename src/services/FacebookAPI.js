import {Facebook} from '@expo';

import {Config} from '@common';
import {toast, log, warn} from '@app/Omni';

class FacebookAPI {
  async login() {
    try {
      console.log('Starting Facebook login...');
      console.log('Using Facebook App ID:', Config.appFacebookId);
      
      const ask = await Facebook.logInWithReadPermissionsAsync(
        Config.appFacebookId,
        {
          permissions: ['public_profile', 'email'],
        },
      );
      
      console.log('Facebook login response:', ask);
      
      if (!ask) {
        console.log('Facebook login returned null');
        return null;
      }

      const {type} = ask;
      console.log('Login type:', type);

      if (type === 'success') {
        const {token} = ask;
        console.log('Got Facebook token:', token);
        return token;
      }
      
      console.log('Facebook login was not successful');
      return null;
    } catch (error) {
      console.error('Facebook login error:', error);
      warn('Facebook login error: ' + error.message);
      return null;
    }
  }

  logout() {
    Facebook.logOut();
  }

  async getAccessToken() {
    return Facebook.getCurrentFacebook();
  }

  async shareLink(link, desc) {
    const shareLinkContent = {
      contentType: 'link',
      contentUrl: link,
      contentDescription: desc,
    };
    try {
      const canShow = await Facebook.canShow(shareLinkContent);
      if (canShow) {
        const result = await Facebook.show(shareLinkContent);
        if (!result.isCancelled) {
          toast('Post shared');
          log(`Share a post with id: ${result.postId}`);
        }
      }
    } catch (error) {
      toast('An error occurred. Please try again later');
      error(`Share post fail with error: ${error}`);
    }
  }
}

export default new FacebookAPI();

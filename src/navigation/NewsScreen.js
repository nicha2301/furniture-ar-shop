import React from 'react';

import {News} from '@containers';

const NewsScreen = React.memo(({navigation}) => {
  return (
    <News
      onViewNewsScreen={post => navigation.navigate('NewsDetailScreen', post)}
    />
  );
});

export default NewsScreen;

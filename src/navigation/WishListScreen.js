import React from 'react';

import {WishList} from '@containers';

const WishListScreen = React.memo(({navigation}) => {
  return (
    <WishList
      onViewProduct={product => navigation.navigate('DetailScreen', product)}
      onViewHome={() => navigation.navigate('Default')}
    />
  );
});

export default WishListScreen;

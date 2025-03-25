import React from 'react';

import {Filters} from '@containers';

const FilterScreen = React.memo(({navigation, route}) => {
  return (
    <Filters
      navigation={navigation}
      route={route}
      onBack={() => navigation.goBack()}
    />
  );
});

export default FilterScreen;

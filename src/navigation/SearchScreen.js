import React from 'react';
import {Search} from '@components';

const SearchScreen = React.memo(({navigation}) => {
  const {navigate, goBack} = navigation;

  return (
    <Search
      onBack={goBack}
      onViewProductScreen={product => navigate('DetailScreen', product)}
      navigation={navigation}
      onFilter={onSearch => navigate('FiltersScreen', {onSearch})}
    />
  );
});

export default SearchScreen;

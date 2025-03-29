import React from 'react';
import {TouchableOpacity, Text} from 'react-native';

import styles from './styles';

const FlatButton = React.memo(({text, load, name}) => {
  return (
    <TouchableOpacity onPress={load} style={styles.button}>
      <Text style={styles.text}>{text}</Text>
    </TouchableOpacity>
  );
});

export default FlatButton;

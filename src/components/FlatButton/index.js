import React from 'react';
import {TouchableOpacity, Text} from 'react-native';

import styles from './styles';

const FlatButton = React.memo(() => {
  return (
    <TouchableOpacity onPress={this.props.load} style={styles.button}>
      <Text style={styles.text}>{this.props.text}</Text>
    </TouchableOpacity>
  );
});

export default FlatButton;

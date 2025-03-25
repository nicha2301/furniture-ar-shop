import React from 'react';
import {View, Text, I18nManager} from 'react-native';
import moment from 'moment';

import {Languages} from '@common';
import styles from './styles';

export default class OrderNotes extends React.PureComponent {
  render() {
    const {
      orderNotes,
      theme: {
        colors: {text},
      },
    } = this.props;

    return (
      <View>
        <View
          style={[
            styles.header,
            I18nManager.isRTL ? {alignItems: 'flex-end'} : {},
          ]}>
          <Text style={styles.label(text)}>{Languages.OrderNotes}</Text>
        </View>
        <View style={styles.addressContainer}>
          {orderNotes &&
            orderNotes.map((item, index) => (
              <View key={index} style={styles.noteItem}>
                <Text style={styles.noteContent(text)}>{item.note}</Text>
                <Text style={styles.noteTime}>
                  {moment(item.date_created).fromNow()}
                </Text>
              </View>
            ))}
        </View>
      </View>
    );
  }
}

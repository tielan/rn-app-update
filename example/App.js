/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import {
  Button,
  StyleSheet,
  View,
  NativeModules
} from 'react-native';

var RNAppUpdate = NativeModules.RNAppUpdate

export default class App extends Component {

  async onUpdateAction() {
    let result = await fetch('http://api.zwfw.hunan.gov.cn/appVersion/checkoutNewApp', {
      method: 'GET',
      headers: {
        'accept': 'application/json',
        'content-type': 'application/json',
        "x-api-key": 'TibfxU7UibQXWojJc2dx2cRQ'
      }
    }).then((response) => response.json());
    console.log(result);
    RNAppUpdate.update(Object.assign({}, {
      downloadUrl: 'http://api.zwfw.hunan.gov.cn/minio/apk/download?url='+result.filePath,
      xApiKey: 'TibfxU7UibQXWojJc2dx2cRQ'
    }, result));
  }
  render() {
    return (
      <View style={styles.container}>
        <Button title="点击" onPress={this.onUpdateAction}></Button>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  Button,
} from 'react-native';

import UpdateManager from 'rn-app-update'

const App = () => {
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <Button title="获取版本号" onPress={() => {
          UpdateManager.loadVersion((data)=>{
            alert(JSON.stringify(data));
          });
        }} />
        <Button title="检查更新" onPress={() => {
          UpdateManager.check({
            mainUrl: 'http://172.16.17.158:8098',
            orgId: '4',
            callback: () => {
              alert('ok')
            }
          });
        }} />
      </SafeAreaView>
    </>
  );
};


export default App;

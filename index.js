import {
    NativeModules,
    Platform
} from 'react-native';

import { Config,Fetch } from 'c2-mobile';


const UpdateManagerModule = NativeModules.UpdateManagerModule;


export default class UpdateManager {
    //自动检测更新
    static init(params = {}) {
        if (Platform.OS === 'android') {
            let url = Config.mainUrl;
            let apiKey = Config.C2XApiKey;
            let manual = 'true';
            UpdateManagerModule.init({ url, apiKey, manual,params});
        } else if (Platform.OS === 'ios') {
            Fetch.getJson('https://itunes.apple.com/cn/lookup?id=1265371343',{}).then((response)=>{
                console.log(response)
            }).catch((error)=>{

            });
        }

    }

    //手动检测更新
    static check(params = {}) {
        if (Platform.OS === 'android') {
            let url = Config.mainUrl;
            let apiKey = Config.C2XApiKey;
            let manual = 'true';
            UpdateManagerModule.init({ url, apiKey, manual ,params});
        }
    }
}
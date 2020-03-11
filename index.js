import {
    NativeModules,
    Platform
} from 'react-native';

const UpdateManagerModule = NativeModules.UpdateManagerModule;
export default class UpdateManager {
    //自动检测更新
    static init({ mainUrl, apiKey = "0", orgId }) {
        if (Platform.OS === 'android') {
            if (mainUrl && orgId) {
                UpdateManagerModule.init({ mainUrl, apiKey, orgId });
            }
        } else if (Platform.OS === 'ios') {

        }

    }
    //手动检测更新
    static check() {

    }
}
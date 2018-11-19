
import { NativeModules } from 'react-native';
const RNDocViewer = NativeModules.RNDocViewer;
export default {
  openDoc: RNDocViewer.openDoc
}

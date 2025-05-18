import { storageManager } from '../utils/storageManager';

export default {
  install: (app) => {
    // Make storageManager available as this.$storage in all components
    app.config.globalProperties.$storage = storageManager;
  }
}; 
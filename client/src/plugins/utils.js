import * as dateUtils from '../utils/dateUtils.js';
import * as imageUtils from '../utils/imageUtils.js';

export default {
    install: app => {
        app.config.globalProperties.$utils = {
            ...dateUtils,
            ...imageUtils
        };
    }
}
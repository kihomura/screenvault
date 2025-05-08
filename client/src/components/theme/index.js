import { CyberpunkBackground } from './cyberpunk';
import { LightBackground } from './light';
import { DarkBackground } from './dark';

export {
  CyberpunkBackground,
  LightBackground,
  DarkBackground
};

export default {
  install: (app) => {
    app.component('CyberpunkBackground', CyberpunkBackground);
    app.component('LightBackground', LightBackground);
    app.component('DarkBackground', DarkBackground);
  }
}; 
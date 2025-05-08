import CyberpunkBackground from './CyberpunkBackground.vue';
import CyberpunkHeading from './CyberpunkHeading.vue';
import CyberpunkCard from './CyberpunkCard.vue';
import CyberpunkButton from './CyberpunkButton.vue';

export {
  CyberpunkBackground,
  CyberpunkHeading,
  CyberpunkCard,
  CyberpunkButton
};

export default {
  install: (app) => {
    app.component('CyberpunkBackground', CyberpunkBackground);
    app.component('CyberpunkHeading', CyberpunkHeading);
    app.component('CyberpunkCard', CyberpunkCard);
    app.component('CyberpunkButton', CyberpunkButton);
  }
}; 
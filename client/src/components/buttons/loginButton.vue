<template>
  <button
      class="superBtn"
      :type="type"
      :disabled="disabled"
      @click="$emit('click')"
  >
    <slot></slot>
  </button>
</template>

<script>
export default {
  name: 'LoginButton',
  props: {
    type: {
      type: String,
      default: "submit"
    },
    disabled: {
      type: Boolean,
      default: false
    }
  }
};
</script>

<style scoped>

button.superBtn {
  color: #1e1e1e;
  text-shadow: 0 -1px 0 rgba(58, 58, 58, 0.8);
  font-weight: bold;
  letter-spacing: 0.08em;
  width: 242px;
  height: 39.6px;
  line-height: 39.6px;
  position: relative;
  border-radius: 30px;
  background-size: 100%;
  background-image: linear-gradient(rgba(195, 205, 205, 0.28), rgb(242, 248, 248));
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6),
  inset 0 -1px 0 rgba(0, 0, 0, 0.3),
  inset 0 0 0px 1px rgba(0, 0, 0, 0.1),
  0 0 1px rgba(0, 0, 0, 0.5),
  0 2px 2px rgba(0, 0, 0, 0.4);
  border: none;
  cursor: pointer;
  padding: 0;
  background-repeat: no-repeat;
  background-position: center;
  transition: all 0.1s ease;
}

button.superBtn:after {
  width: 253px;
  height: 49.5px;
  top: -4.95px;
  margin-left: 50%;
  left: -126.5px;
  color: #dcdcdc;
  content: "";
  display: block;
  border-radius: 30px;
  background-size: 100%;
  background-image: linear-gradient(#757575, #1b1b1b);
  box-shadow: 0 -1px 1px rgba(0, 0, 0, 0.7),
  0 1px 1px rgba(255, 255, 255, 0.4),
  0 0 1px 1px rgba(0, 0, 0, 0.3),
  inset 0 0px 3px rgba(0, 0, 0, 0.4),
  inset 0 1px 2px rgba(0, 0, 0, 0.6);
  position: absolute;
  z-index: -1;
}

button.superBtn:hover {
  color: #00ccff;
  text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.3),
  0 0px 30px rgba(0, 255, 255, 0.7);
  background: #333;
  box-shadow: 0 1px 0px rgba(255, 255, 255, 0.4),
  inset 0 3px 3px rgba(0, 0, 0, 0.7),
  inset 0 0 4px rgba(0, 0, 0, 0.9),
  0 0 0 black;
}

button.superBtn:hover:after {
  box-shadow: 0 -1px 1px rgba(0, 0, 0, 0.7),
  0 1px 1px rgba(255, 255, 255, 0.4),
  0 0 1px 1px rgba(0, 0, 0, 0.3),
  inset 0 0px 3px rgba(0, 0, 0, 0.4),
  inset 0 1px 2px rgba(0, 0, 0, 0.6),
  0 0 15px 5px rgba(0, 255, 255, 0.2);
}

button.superBtn:disabled {
  color: #b8b8b8;
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

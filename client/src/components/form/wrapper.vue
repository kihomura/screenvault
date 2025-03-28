<template>
  <div class="wrapper" :style="wrapperStyle">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: "wrapper",
  props: {
    width: {
      type: String,
      default: ""
    }
  },
  computed: {
    wrapperStyle() {
      return this.width ? { width: this.width, margin: "0 auto" } : {};
    }
  }
};
</script>

<style scoped>
.wrapper {
  position: relative;
  min-height: 400px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>

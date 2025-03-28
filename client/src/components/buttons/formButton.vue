<template>
  <button
      :type="type"
      @click="$emit('click')"
      class="form-btn">
    <slot></slot>
  </button>
</template>

<script>
export default {
  name: 'FormButton',
  props: {
    type: { type: String, default: 'button' }
  }
}
</script>

<style scoped>
.form-btn {
  width: 100%;
  height: 45px;
  background: #f2f2f2;
  border: none;
  outline: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1em;
  color: #5c5c5c;
  font-weight: 500;
  transition: .5s;
}
.form-btn:hover {
  background-color: #515151;
  color: white;
}
</style>

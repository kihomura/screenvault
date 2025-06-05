<template>
  <div class="message" v-if="message">
    {{ message }}
  </div>
</template>

<script>
export default {
  name: 'MessageDisplay',
  props: {
    message: { type: String, default: '' }
  }
}
</script>

<style scoped>
.message {
  color: #f34646;
  font-size: .8em;
  text-align: center;
  margin: 25px 0 10px;
}
</style>

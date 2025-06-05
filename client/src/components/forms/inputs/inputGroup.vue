<template>
  <div class="form-group">
    <input
        :type="type"
        :id="id"
        :name="name"
        :required="required"
        :placeholder="placeholder"
        :value="modelValue"
        @input="onInput"
        @focus="$emit('focus', $event)"
        @blur="$emit('blur', $event)"
    />
    <label :for="id">{{ label }}</label>
  </div>
</template>

<script>
export default {
  name: "FormGroup",
  props: {
    id: { type: String, required: true },
    name: { type: String, required: true },
    label: { type: String, required: true },
    type: { type: String, default: "text" },
    required: { type: Boolean, default: true },
    placeholder: { type: String, default: "" },
    modelValue: { type: String, default: "" }
  },
  emits: ["update:modelValue", "focus", "blur"],
  methods: {
    onInput(e) {
      this.$emit("update:modelValue", e.target.value);
    }
  }
};
</script>

<style scoped>
.form-group {
  position: relative;
  width: 100%;
  height: 50px;
  border-bottom: 2px solid #ddd;
  margin-bottom: 15px;
}

.form-group label {
  position: absolute;
  top: 50%;
  left: 5px;
  transform: translateY(-50%);
  font-size: 1em;
  color: #666;
  font-weight: 500;
  pointer-events: none;
  transition: 0.5s;
}

.form-group input:focus ~ label,
.form-group input:valid ~ label {
  top: -5px;
  font-size: 0.8em;
  color: #999;
}

.form-group input {
  width: 100%;
  height: 100%;
  background: transparent;
  border: none;
  outline: none;
  font-size: 1em;
  color: #ffffff;
  padding: 0 5px;
}
</style>

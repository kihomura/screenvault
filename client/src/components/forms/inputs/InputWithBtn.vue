<template>
  <div class="input-button">
    <div class="field">
      <input
          :id="id"
          type="text"
          :maxlength="maxlength"
          v-model="inputValue"
          @keyup="handleKeyup"
          :class="{ active: inputValue.length > 0 }"
          @keyup.enter="handleButtonClick"
      />
      <label :for="id" :class="{ active: inputValue.length > 0 }">
        <span>{{ placeholder }}</span>
      </label>
      <button
          :class="{ active: inputValue.length > 0, full: isButtonFull }"
          @click="handleButtonClick"
      >
        {{ buttonText }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'InputButton',
  props: {
    id: {
      type: String,
      default: 'custom-input'
    },
    maxlength: {
      type: String,
      default: '50'
    },
    placeholder: {
      type: String,
      default: 'Enter text'
    },
    buttonLabel: {
      type: String,
      default: 'OK'
    },
    successText: {
      type: String,
      default: 'Done!'
    },
    resetDelay: {
      type: Number,
      default: 1200
    },
    value: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      inputValue: this.value,
      isButtonFull: false,
      buttonText: this.buttonLabel
    };
  },
  watch: {
    value(newVal) {
      this.inputValue = newVal;
    }
  },
  methods: {
    handleKeyup() {
      this.$emit('input', this.inputValue);
    },
    handleButtonClick() {
      if (!this.inputValue.trim()) return;

      this.isButtonFull = true;
      this.buttonText = this.successText;

      this.$emit('button-click', this.inputValue);

      setTimeout(() => {
        this.inputValue = '';
        this.isButtonFull = false;
        this.buttonText = this.buttonLabel;
        this.$emit('input', '');
      }, this.resetDelay);
    }
  }
}
</script>

<style scoped>
.input-button {
  display: flex;
  width: 100%;
}

.field {
  position: relative;
  width: 100%;
  height: 55px;
}

.field input {
  width: 100%;
  height: 100%;
  border: 2px solid var(--border-light);
  border-radius: var(--border-radius-md);
  background-color: var(--background-base);
  padding: 8px 120px 8px 16px;
  font-size: 16px;
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.field input:focus {
  outline: none;
  border-color: var(--highlight);
  box-shadow: 0 0 0 3px rgba(174, 202, 95, 0.2);
}

.field input,
.field button {
  position: absolute;
}

.field button {
  background-color: rgba(0, 0, 0, 0.1);
  right: 0;
  height: 100%;
  border: none;
  border-radius: 0 var(--border-radius-md) var(--border-radius-md) 0;
  width: 110px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0.6;
  color: var(--text-muted);
  pointer-events: none;
}

.field button.active {
  background-color: var(--highlight);
  color: white;
  opacity: 1;
  pointer-events: auto;
}

.field button.active:hover {
  background-color: var(--primary);
}

.field button.full {
  width: 100%;
  border-radius: var(--border-radius-md);
}

.field label {
  position: absolute;
  color: var(--text-muted);
  transform: translate(16px, 12px);
  transition: all 0.3s ease;
  font-size: 16px;
  pointer-events: none;
}

.field label.active {
  display: none;
}

.field input:focus + label {
  display: none;
}

.field input:focus + label + button {
  opacity: 1;
}
</style>
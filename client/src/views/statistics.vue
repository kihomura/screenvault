<template>
  <div class="button-examples">
    <h2>按钮类型示例</h2>
    <div class="button-row">
      <main-btn type="primary">主要按钮</main-btn>
      <main-btn type="secondary">次要按钮</main-btn>
      <main-btn type="tertiary">第三级按钮</main-btn>
      <main-btn type="danger">删除按钮</main-btn>
      <main-btn type="highlight">醒目按钮</main-btn>
    </div>

    <h2>其他类型按钮</h2>
    <div class="button-row">
      <main-btn type="warning">警告按钮</main-btn>
      <main-btn type="info">信息按钮</main-btn>
      <main-btn type="success">成功按钮</main-btn>
      <main-btn type="text">文本按钮</main-btn>
    </div>

    <h2>按钮尺寸</h2>
    <div class="button-row">
      <main-btn type="primary" size="small">小按钮</main-btn>
      <main-btn type="primary">中按钮</main-btn>
      <main-btn type="primary" size="large">大按钮</main-btn>
    </div>

    <h2>按钮状态</h2>
    <div class="button-row">
      <main-btn type="primary" disabled>禁用状态</main-btn>
      <main-btn type="primary" loading>加载状态</main-btn>
    </div>

    <h2>带图标的按钮</h2>
    <div class="button-row">
      <main-btn type="primary" icon>
        <template #icon>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
            <path d="M15.964.686a.5.5 0 0 0-.65-.65L.767 5.855H.766l-.452.18a.5.5 0 0 0-.082.887l.41.26.001.002 4.995 3.178 3.178 4.995.002.002.26.41a.5.5 0 0 0 .886-.083l6-15Zm-1.833 1.89L6.637 10.07l-.215-.338a.5.5 0 0 0-.154-.154l-.338-.215 7.494-7.494 1.178-.471-.47 1.178Z"/>
          </svg>
        </template>
        发送
      </main-btn>
      <main-btn type="danger" icon>
        <template #icon>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
            <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/>
            <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/>
          </svg>
        </template>
        删除
      </main-btn>
    </div>

    <h2>块级按钮</h2>
    <div class="button-column">
      <main-btn type="primary" block>块级主要按钮</main-btn>
      <main-btn type="secondary" block>块级次要按钮</main-btn>
      <main-btn type="highlight" block>块级醒目按钮</main-btn>
    </div>
  </div>
</template>

<script>
import MainBtn from '../components/buttons/MainBtn.vue';

export default {
  name: 'ButtonExample',
  components: {
    MainBtn
  }
}
</script>

<style scoped>
.button-examples {
  font-family: var(--font-fontFamily-primary);
  color: var(--text-primary);
  padding: var(--spacing-xl);
}

h2 {
  margin-top: var(--spacing-xl);
  margin-bottom: var(--spacing-md);
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-semibold);
}

.button-row {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.button-column {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}
</style>
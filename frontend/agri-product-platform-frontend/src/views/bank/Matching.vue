<template>
  <div class="page-placeholder">
    <div class="placeholder-icon">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <circle cx="12" cy="12" r="3"/>
        <path d="M12 1v6m0 6v6"/>
        <path d="m5 12 6-6m0 12 6-6"/>
        <circle cx="12" cy="12" r="9"/>
      </svg>
    </div>
    <h2>智能匹配</h2>
    <p>银行智能匹配页面</p>
  </div>
</template>

<style scoped>
.page-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  text-align: center;
  color: var(--color-text-tertiary, #6b7280);
}

.placeholder-icon {
  width: 64px;
  height: 64px;
  margin-bottom: var(--spacing-4, 16px);
  opacity: 0.5;
}

.placeholder-icon svg {
  width: 100%;
  height: 100%;
}

.page-placeholder h2 {
  font-size: var(--font-size-xl, 24px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-2, 8px);
}

.page-placeholder p {
  font-size: var(--font-size-sm, 14px);
  margin: 0;
}
</style>

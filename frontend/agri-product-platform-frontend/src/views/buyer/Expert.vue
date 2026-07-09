<template>
  <div class="page-placeholder">
    <div class="placeholder-icon">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <circle cx="12" cy="8" r="3"/>
        <path d="M8 21v-5a4 4 0 014-4M8 21h8M12 12v5M8 10l2-2M16 10l-2-2"/>
      </svg>
    </div>
    <h2>专家咨询</h2>
    <p>买家专家咨询页面</p>
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

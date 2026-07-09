<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <div class="footer-section">
        <div class="footer-logo">
          <div class="footer-logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M12 2v10M12 12c0 4 2 6 4 8M12 12c0 4-2 6-4 8"/>
              <circle cx="12" cy="12" r="2" fill="currentColor"/>
            </svg>
          </div>
          <span>农融汇</span>
        </div>
        <p class="footer-desc">连接农户与市场，助力农业发展</p>
      </div>
      <div class="footer-links">
        <div class="footer-link-group">
          <h4>产品服务</h4>
          <a href="/market">农产品交易</a>
          <a href="/finance-products">融资服务</a>
          <a href="/expert-help">专家咨询</a>
        </div>
        <div class="footer-link-group">
          <h4>关于我们</h4>
          <a href="#">平台介绍</a>
          <a href="#">联系我们</a>
          <a href="#">帮助中心</a>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <p>&copy; 2024 农融汇. All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup>
// Footer 组件 - 纯展示，无需额外逻辑
</script>

<style scoped>
.app-footer {
  padding: 48px 32px 24px;
  background: var(--color-text);
  color: var(--color-white);
}

.footer-inner {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  margin-bottom: 32px;
}

.footer-section {
  max-width: 300px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: var(--font-semibold);
  margin-bottom: 16px;
}

.footer-logo-icon {
  width: 32px;
  height: 32px;
  color: var(--color-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-desc {
  font-size: 14px;
  opacity: 0.8;
  line-height: 1.6;
  margin: 0;
}

.footer-links {
  display: flex;
  gap: 64px;
}

.footer-link-group h4 {
  font-size: 14px;
  font-weight: var(--font-semibold);
  margin: 0 0 16px;
}

.footer-link-group a {
  display: block;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 12px;
  transition: color var(--transition-base);
  text-decoration: none;
}

.footer-link-group a:hover {
  color: var(--color-white);
}

.footer-bottom {
  max-width: 1280px;
  margin: 0 auto;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
}

.footer-bottom p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

@media (max-width: 768px) {
  .footer-inner {
    flex-direction: column;
    gap: 32px;
  }

  .footer-links {
    flex-direction: column;
    gap: 24px;
  }
}
</style>

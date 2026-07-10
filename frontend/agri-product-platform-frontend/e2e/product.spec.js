import { test, expect } from '@playwright/test'

test.describe('商品浏览', () => {
  test.beforeEach(async ({ page }) => {
    // 登录
    await page.goto('/login')
    await page.fill('input[name="userName"]', 'test_farmer')
    await page.fill('input[name="password"]', '123456')
    await page.selectOption('select[name="role"]', 'farmer')
    await page.click('button[type="submit"]')
    await page.waitForURL('/')
  })

  test('浏览商品列表', async ({ page }) => {
    await page.goto('/product')
    await expect(page.locator('.product-card').first()).toBeVisible()
  })

  test('搜索商品', async ({ page }) => {
    await page.goto('/product')
    await page.fill('input[placeholder*="商品名称"]', '大米')
    await page.click('button:has-text("搜索")')

    await expect(page.locator('.product-card').first()).toBeVisible()
  })

  test('加入购物车', async ({ page }) => {
    await page.goto('/product')
    await page.click('.product-card:first-child .el-button:has-text("加入购物车")')

    await expect(page.locator('.el-message--success')).toContainText('加入购物车')
  })
})

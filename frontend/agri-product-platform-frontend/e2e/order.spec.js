import { test, expect } from '@playwright/test'

test.describe('订单管理', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[name="userName"]', 'test_buyer')
    await page.fill('input[name="password"]', '123456')
    await page.selectOption('select[name="role"]', 'buyer')
    await page.click('button[type="submit"]')
    await page.waitForURL('/')
  })

  test('查看订单列表', async ({ page }) => {
    await page.goto('/order')
    await expect(page.locator('text=我的订单')).toBeVisible()
  })
})

import { test, expect } from '@playwright/test'

test.describe('购物车', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[name="userName"]', 'test_buyer')
    await page.fill('input[name="password"]', '123456')
    await page.selectOption('select[name="role"]', 'buyer')
    await page.click('button[type="submit"]')
    await page.waitForURL('/')
  })

  test('查看购物车', async ({ page }) => {
    await page.goto('/cart')
    await expect(page.locator('text=购物车')).toBeVisible()
  })

  test('修改商品数量', async ({ page }) => {
    await page.goto('/cart')
    const quantityInput = page.locator('.el-input-number').first()
    await quantityInput.click()
    await page.keyboard.press('ArrowUp')

    await expect(page.locator('.el-message--success')).toBeVisible()
  })
})

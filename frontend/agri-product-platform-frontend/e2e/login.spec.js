import { test, expect } from '@playwright/test'

test.describe('用户登录', () => {
  test('成功登录', async ({ page }) => {
    await page.goto('/login')

    await page.fill('input[name="userName"]', 'test_farmer')
    await page.fill('input[name="password"]', '123456')
    await page.selectOption('select[name="role"]', 'farmer')
    await page.click('button[type="submit"]')

    await expect(page).toHaveURL('/')
    await expect(page.locator('.user-name')).toContainText('测试')
  })

  test('登录失败 - 错误密码', async ({ page }) => {
    await page.goto('/login')

    await page.fill('input[name="userName"]', 'test_farmer')
    await page.fill('input[name="password"]', 'wrong')
    await page.click('button[type="submit"]')

    await expect(page.locator('.el-message--error')).toBeVisible()
  })
})

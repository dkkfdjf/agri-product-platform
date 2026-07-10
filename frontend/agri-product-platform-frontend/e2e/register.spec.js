import { test, expect } from '@playwright/test'

test.describe('用户注册', () => {
  test('成功注册农户', async ({ page }) => {
    await page.goto('/register')

    const timestamp = Date.now()
    await page.fill('input[name="userName"]', `farmer_${timestamp}`)
    await page.fill('input[name="password"]', '123456')
    await page.fill('input[name="confirmPassword"]', '123456')
    await page.fill('input[name="realName"]', '测试农户')
    await page.fill('input[name="phone"]', '13800138000')
    await page.selectOption('select[name="role"]', 'farmer')
    await page.click('button[type="submit"]')

    await expect(page).toHaveURL('/login')
    await expect(page.locator('.el-message--success')).toContainText('注册成功')
  })

  test('密码不一致', async ({ page }) => {
    await page.goto('/register')

    await page.fill('input[name="password"]', '123456')
    await page.fill('input[name="confirmPassword"]', '654321')
    await page.click('button[type="submit"]')

    await expect(page.locator('.el-message--error')).toBeVisible()
  })
})

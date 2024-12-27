import router from '@/router'
import { message } from 'ant-design-vue'
import checkAccess from './checkAccess'
import { useLoginUserStore } from '@/stores/useLoginUserStore'

let firstFetchLoginUser: boolean = true // 是否为首次获取用户信息

/**
 * 全局权限校验 (在用户进入页面前 判断用户权限)
 */
router.beforeEach(async (to, from, next) => {
  // 用户权限
  const loginUserStore = useLoginUserStore() // 全局状态
  let loginUser = loginUserStore.loginUser

  // 确保页面刷新时 首次加载时 能等待后端返回用户信息后再校验权限
  if (firstFetchLoginUser) {
    await loginUserStore.fetchLoginUser() // 后端接口
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false // flag
  }

  // 页面权限
  // 自定义权限校验规则 管理员才能访问/admin
  const toUrl = to.fullPath
  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.userRole !== 'admin') {
      message.error('你没有权限访问该页面')
      next('/error/403')
      return
    }
  }
  next() // 放行
})

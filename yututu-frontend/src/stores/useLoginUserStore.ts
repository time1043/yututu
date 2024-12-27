import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUserUsingGet } from '@/api/userController'

/**
 * 状态管理 (登录的用户信息)
 */
export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.LoginUserVO>({ userName: '未登录' }) // 全局状态初始化

  // 后端获取登录用户信息
  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet() // 后端接口
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data // 全局状态设置
    }

    // // mock 5m后自动登陆
    // setTimeout(() => {
    //   loginUser.value.id = 1 // 凭证
    //   loginUser.value.userName = 'oswin902'
    //   loginUser.value.userAvatar = 'https://avatars.githubusercontent.com/u/132178516?v=4'
    // }, 5000)
  }

  /**
   * 设置登录用户
   * @param newLoginUser 登录用户信息
   */
  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser // 全局状态设置
  }

  return { loginUser, fetchLoginUser, setLoginUser }
})

// import { useLoginUserStore } from '@/stores/useLoginUserStore'
// const loginUserStore = useLoginUserStore()
// loginUserStore.fetchLoginUser()

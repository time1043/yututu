<template>
  <!-- 表单form https://antdv.com/components/form-cn/ -->
  <div id="userLoginPage">
    <!-- 标题 -->
    <h2 class="title">鱼图图 - 用户登陆</h2>
    <div class="desc">企业级智能协作云图库</div>

    <!-- 登陆表单 -->
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item
        name="userAccount"
        :rules="[
          { required: true, message: '用户账号是必填项' },
          { min: 4, message: '用户名长度不能少于4位' },
        ]"
      >
        <a-input v-model:value="formState.userAccount" placeholder="请输入用户账号" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '用户密码是必填项' },
          { min: 6, message: '密码长度不能少于6位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入用户密码" />
      </a-form-item>

      <div class="tips">没有账号? <RouterLink to="/user/register">去注册</RouterLink></div>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登陆</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { userLoginUsingPost } from '@/api/userController'
import router from '@/router'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { message } from 'ant-design-vue'
import { reactive } from 'vue'

// 全局状态管理
const loginUserStore = useLoginUserStore()

// 用于接受用户表单的值
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

/**
 * 登陆表单提交
 * @param values 用户表单值
 */
const handleSubmit = async (values: any) => {
  const res = await userLoginUsingPost(values) // 后端接口

  // 登陆成功 (登录态保存在全局状态中)
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登陆成功')

    // 重定向 首页
    const redirect = new URLSearchParams(window.location.search).get('redirect')
    if (redirect) window.location.href = redirect
    else router.push({ path: '/', replace: true })
  } else {
    message.error('登陆失败 ' + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  max-width: 360px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 5px;
}

.title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.tips {
  text-align: right;
  color: #bbb;
  margin-bottom: 16px;
}
</style>

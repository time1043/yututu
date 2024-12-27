<template>
  <!-- 表单form https://antdv.com/components/form-cn/ -->
  <div id="userRegisterPage">
    <!-- 标题 -->
    <h2 class="title">鱼图图 - 用户注册</h2>
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

      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '确认密码是必填项' },
          { min: 6, message: '密码长度不能少于6位' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请输入确认密码" />
      </a-form-item>

      <div class="tips">已有账号? <RouterLink to="/user/login">去登陆</RouterLink></div>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { userRegisterUsingPost } from '@/api/userController'
import router from '@/router'
import { message } from 'ant-design-vue'
import { reactive } from 'vue'

// 用于接受用户表单的值
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

// 注册表单提交
const handleSubmit = async (values: any) => {
  // 检验参数 两次密码不一致
  if (values.userPassword !== values.checkPassword) {
    message.error('两次密码不一致')
    return
  }

  const res = await userRegisterUsingPost(values) // 后端接口

  // 注册成功 (跳转到登陆页面)
  if (res.data.code === 0 && res.data.data) {
    message.success('注册成功')
    router.push({ path: '/user/login', replace: true })
  } else {
    message.error('注册失败 ' + res.data.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
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

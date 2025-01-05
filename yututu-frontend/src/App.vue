<template>
  <div id="app">
    <a-config-provider :locale="locale">
      <component :is="currentLayout"></component>
    </a-config-provider>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import ZeroLayout from '@/layouts/ZeroLayout.vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

// 默认中文
// https://antdv.com/docs/vue/i18n-cn
const locale = ref(zhCN)

// // 后端获取当前用户信息
// const loginUserStore = useLoginUserStore()
// loginUserStore.fetchLoginUser()

// 当前布局判断
const route = useRoute()
const currentLayout = computed(() => {
  // if (route.path === '/user/login') return ZeroLayout
  const whiteList = [
    '/user/login',
    '/user/register',
    '/error/401',
    '/error/403',
    '/error/404',
    '/error/500',
  ]
  if (whiteList.includes(route.path)) {
    return ZeroLayout
  }
  return BasicLayout
})
</script>

<style scoped></style>

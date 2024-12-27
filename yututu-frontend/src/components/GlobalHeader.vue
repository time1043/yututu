<template>
  <!-- Grid布局 https://antdv.com/components/grid-cn/#components-grid-demo-flex-stretch -->
  <div id="globalHeader">
    <a-row :wrap="false">
      <!-- 图标和标题 -->
      <a-col flex="150px">
        <router-link to="/">
          <div class="title-bar">
            <img src="@/assets/ai-icon64.ico" alt="/user/logo" class="logo" />
            <div class="title">鱼图图</div>
          </div>
        </router-link>
      </a-col>

      <!-- 菜单 https://antdv.com/components/menu-cn/ -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMeanClick"
        />
      </a-col>

      <!-- 登陆跳转 or 用户信息 -->
      <!-- 头像 https://antdv.com/components/avatar-cn/ -->
      <!-- 下拉菜单 https://antdv.com/components/dropdown-cn/ -->
      <a-col flex="130px">
        <div class="user-login-status" style="text-align: right">
          <!-- 已登录 -->
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                {{ loginUserStore.loginUser.userName ?? '无名' }}
                <a-avatar :src="loginUserStore.loginUser.userAvatar" :size="48" />
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="showLogout"> <LogoutOutlined /> 退出登录 </a-menu-item>
                  <a-menu-item @click="goUserCenter"> <UserOutlined /> 个人中心 </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <!-- 未登录 -->
          <div v-else>
            <a-button type="primary" href="/user/login">登陆</a-button>
          </div>
        </div>
      </a-col>
    </a-row>

    <a-modal v-model:open="openLoyout" title="确认提醒" @ok="doLogout">
      <p>确定退出登陆</p>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  HomeOutlined,
  LogoutOutlined,
  UserOutlined,
  UserSwitchOutlined,
  LinkOutlined,
} from '@ant-design/icons-vue' // https://antdv.com/components/icon-cn
import { message, type MenuProps } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { userLogoutUsingPost } from '@/api/userController'

const current = ref<string[]>(['']) // 高亮显示
const router = useRouter() // 路由跳转
const loginUserStore = useLoginUserStore() // 状态管理 当前用户

// 路由信息 (未经过滤的菜单项)
const originItems = [
  { key: '/', icon: () => h(HomeOutlined), label: '主页', title: '主页' },
  { key: '/add_picture', label: '创建图片', title: '创建图片' },
  { key: '/admin/userManage', icon: h(UserSwitchOutlined), label: '用户管理', title: '用户管理' },
  { key: '/admin/pictureManage', label: '图片管理', title: '图片管理' },
  { key: '/admin/spaceManage', label: '空间管理', title: '空间管理' },
  { key: '/about', icon: h(LinkOutlined), label: '其他', title: '其他' },
  // {
  //   key: '/others',
  //   label: h('a', { href: 'https://github.com/time1043', target: '_blank' }, 'time1043'),
  //   title: '其他',
  // },
]
// 路由信息 (根据权限过滤菜单项)
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    // 管理员才能看到 /admin 开头的菜单
    // if (menu?.key?.startsWith('/admin')) {
    if (menu?.key && (menu?.key as string).startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') return false
    }
    return true // 放行
  })
}
// 展示在菜单的路由数组
// const items = ref<MenuProps['items']>([ { key: '/', label: '主页', title: '主页', icon: h(HomeOutlined) }, ])
const items = computed(() => filterMenus(originItems))

/**
 * 路由跳转事件
 * https://antdv.com/components/menu-cn/#api
 *
 * @param item 菜单项
 * @param key 菜单项的key
 * @param keyPath 菜单项的key路径
 */
const doMeanClick = ({ item, key, keyPath }: any) => {
  router.push({ path: key })
  //current.value = [key]  // 高亮显示
}

/**
 * 钩子 每次跳转到新页面前 都会执行
 *
 * @param to 新页面
 * @param from 旧页面
 * @param next 后续处理
 */
router.afterEach((to, from, next) => {
  current.value = [to.path] // 高亮显示
})

/**
 * 退出登录
 */
const openLoyout = ref(false)
const showLogout = () => {
  openLoyout.value = true
}
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({ userName: '未登录' })
    message.success('退出登录成功')
    await router.push({ path: '/user/login' })
  } else {
    message.error('退出登录失败 ' + res.data.message)
  }
  openLoyout.value = false // flag
}

/**
 * 前往个人中心
 */
const goUserCenter = () => {
  router.push({ path: '/user/center' })
}
</script>

<style scoped>
#globalHeader .title-bar {
  display: flex;
  align-items: center;
}

#globalHeader .title-bar .logo {
  width: 48px;
  height: 48px;
}

#globalHeader .title-bar .title {
  font-size: 24px;
  margin-left: 10px;
  color: rgb(86, 21, 203);
}
</style>

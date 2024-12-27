import { createRouter, createWebHistory } from 'vue-router'
import ACCESS_ENUM from '@/access/accessEnum'
// 用户模块
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserCenterPage from '@/pages/user/UserCenterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue' // 管理
// 图片模块
import AddPicturePage from '@/pages/picture/AddPicturePage.vue'
import PictureDetailPage from '@/pages/picture/PictureDetailPage.vue'
import AddPictureBatchPage from '@/pages/picture/AddPictureBatchPage.vue'
import PictureManagePage from '@/pages/admin/PictureManagePage.vue' // 管理
// 其他模块
import HomePage from '@/pages/HomePage.vue'
import Error403Page from '@/pages/error/403.vue'
import Error404Page from '@/pages/error/404.vue'
import Error500Page from '@/pages/error/500.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '主页',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegisterPage,
    },
    {
      path: '/user/center',
      name: '用户中心',
      component: UserCenterPage,
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManagePage,
      meta: { access: ACCESS_ENUM.ADMIN },
    },
    {
      path: '/admin/pictureManage',
      name: '图片管理',
      component: PictureManagePage,
    },
    {
      path: '/add_picture',
      name: '创建图片',
      component: AddPicturePage,
    },
    {
      path: '/add_picture/batch',
      name: '批量创建图片',
      component: AddPictureBatchPage,
    },
    {
      path: '/picture/:id',
      name: '图片详情',
      component: PictureDetailPage,
      props: true,
    },
    {
      path: '/about',
      name: '关于',
      component: () => import('../pages/AboutPage.vue'), // lazy-loaded
    },
    {
      path: '/error/403',
      name: '无权限',
      component: Error403Page,
    },
    {
      path: '/error/404',
      name: '未找到',
      component: Error404Page,
    },
    {
      path: '/error/500',
      name: '服务器错误',
      component: Error500Page,
    },
  ],
})

export default router

<template>
  <!-- https://antdv.com/components/layout-cn -->
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <!-- 顶部：导航 -->
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>

      <!-- 中间：内容 vue-router -->
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>

      <!-- 底部：版权信息 -->
      <a-layout-footer class="footer">
        <a href="http://github.com/time1043" target="_blank"> <GithubOutlined /> </a>
        yututu by oswin902
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import { GithubOutlined } from '@ant-design/icons-vue'
</script>

<style scoped>
#basicLayout .header {
  background: #fff;
  color: unset;
  margin-bottom: 16px;
  padding-inline: 20px;
  /* 绝对定位 */
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
}

#basicLayout .content {
  padding: 20px;
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 30px;
  margin-top: 80px;
  margin-left: 20px;
  margin-right: 20px;
}

#basicLayout .footer {
  text-align: center;
  background: #efefef;
  padding: 16px;
  /* 绝对定位 */
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>

<template>
  <!-- https://antdv.com/components/layout-cn -->
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <!-- 中间：内容 vue-router -->
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>

      <!-- 底部：版权信息 -->
      <a-layout-footer class="footer">
        <a href="http://github.com/time1043" target="_blank"> yututu by oswin902 </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
#basicLayout .content {
  padding: 20px;
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 30px;
}

#basicLayout .footer {
  text-align: center;
  background: #efefef;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>

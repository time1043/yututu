<template>
  <a-result status="403" title="401" sub-title="未登录, 请登录后操作">
    <template #extra>
      <a-button type="primary" href="/user/login">去登录</a-button>
    </template>
  </a-result>
</template>

<script setup lang="ts"></script>

<style scoped></style>

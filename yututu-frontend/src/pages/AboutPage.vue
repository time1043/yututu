<template>
  <div class="about">
    <h1>This is an about page</h1>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { healthUsingGet } from '@/api/mainController'

// 钩子 页面加载时 执行
onMounted(async () => {
  await healthUsingGet().then((res) => {
    console.log(res.data?.data)
  })
})
</script>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}
</style>

<!-- <template>
  <a-table :row-selection="rowSelection" :columns="columns" :data-source="dataList">
    <template #bodyCell="{ column, text }">
      <template v-if="column.dataIndex === 'name'">
        <a>{{ text }}</a>
      </template>
    </template>
  </a-table>
</template>
<script lang="ts" setup>
import { listUserVoByPageUsingPost } from '@/api/userController'
import { message } from 'ant-design-vue'
import { onMounted, reactive, ref } from 'vue'

// 页面加载时获取数据，请求一次
onMounted(() => {
  fetchData()
})

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// const data: DataType[] = [
//   {
//     key: '1',
//     name: 'John Brown',
//     age: 32,
//     address: 'New York No. 1 Lake Park',
//   },
//   {
//     key: '2',
//     name: 'Jim Green',
//     age: 42,
//     address: 'London No. 1 Lake Park',
//   },
//   {
//     key: '3',
//     name: 'Joe Black',
//     age: 32,
//     address: 'Sidney No. 1 Lake Park',
//   },
//   {
//     key: '4',
//     name: 'Disabled User',
//     age: 99,
//     address: 'Sidney No. 1 Lake Park',
//   },
// ]
const dataList = ref<API.UserVO[]>([])
const total = ref<number>(0)

// 搜索条件
// reactive 响应式变量 对象中任何一个属性发生变化都会触发重新渲染
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'ascend',
  userRole: '',
  id: undefined,
})

/**
 * 获取数据
 */
const fetchData = async () => {
  const res = await listUserVoByPageUsingPost({ ...searchParams }) // 后端接口
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = Number(res.data.data.total) ?? 0
  } else {
    message.error('获取数据失败 ' + res.data.message)
  }
}

const rowSelection = {}
</script> -->

<template>
  <div id="userManagePage">
    <!-- 上方 表单搜索栏 https://antdv.com/components/form-cn/ -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <!-- <a-form-item>
        <a-radio-group v-model:value="searchParams.userRole">
          <a-radio-button value="user">普通用户</a-radio-button>
          <a-radio-button value="vip">VIP</a-radio-button>
          <a-radio-button value="svip">SVIP</a-radio-button>
          <a-radio-button value="admin">管理员</a-radio-button>
        </a-radio-group>
      </a-form-item> -->

      <a-form-item>
        <a-input v-model:value="searchParams.id" placeholder="输入ID" allow-clear />
      </a-form-item>

      <a-form-item>
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" allow-clear />
      </a-form-item>

      <a-form-item>
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" allow-clear />
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" ghost @click="doReset">重置</a-button>
      </a-form-item>
      <a-form-item>
        <a-button danger type="primary" @click="doReset">删除</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 16px" />

    <!-- 下方 表格(支持分页) https://antdv.com/components/table-cn/ -->
    <!-- :scroll="{ x: '0', y: '700px' }" -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <!-- 表格体 插槽 -->
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>

        <template v-else-if="column.dataIndex === 'id'">
          <a-typography-paragraph :copyable="{ tooltip: false }">
            {{ record.id }}
          </a-typography-paragraph>
        </template>

        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else-if="record.userRole === 'vip'">
            <a-tag color="yellow">VIP</a-tag>
          </div>
          <div v-else-if="record.userRole === 'svip'">
            <a-tag color="yellow">SVIP</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>

        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>

        <!-- 编辑的内容回显 -->
        <template v-if="['userName', 'userProfile'].includes(column.dataIndex)">
          <div>
            <a-input
              v-if="editableData[record.id]"
              v-model:value="
                editableData[record.id][column.dataIndex as keyof API.UserUpdateRequest]
              "
              style="margin: -5px 0"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-else-if="column.key === 'action'">
          <!-- <a-button danger @click="doDelete(record.id)"> 删除 </a-button> -->
          <a-button danger>
            <a-popconfirm v-if="dataList.length" title="确定删除?" @confirm="doDelete(record.id)">
              <a> 删除 </a>
            </a-popconfirm>
          </a-button>

          <div class="editable-row-operations">
            <span v-if="editableData[record.id]">
              <a-button type="dashed">
                <a-popconfirm title="确定保存?" @confirm="doSave(record.id)">
                  <a> 保存 </a>
                </a-popconfirm>
              </a-button>

              <a-button type="dashed">
                <a-typography-link @click="doCancel(record.id)">取消</a-typography-link>
              </a-button>
            </span>

            <span v-else>
              <a-button primary>
                <a @click="doEdit(record.id)">编辑</a>
              </a-button>
            </span>
          </div>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import type { UnwrapRef } from 'vue'
import { cloneDeep } from 'lodash-es' // npm i --save-dev @types/lodash-es
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  deleteUserUsingPost,
  listUserVoByPageUsingPost,
  updateUserUsingPost,
} from '@/api/userController'

// 页面加载时获取数据，请求一次
onMounted(() => {
  fetchData()
})

// 定义列名
const columns = [
  {
    title: 'id',
    dataIndex: 'id', // dataIndex 后端对应
    width: 90, // 宽度
    // ellipsis: true, // 显示省略号
    sorter: {
      compare: (a: any, b: any) => a.id - b.id,
      multiple: 3, // 数值越大 优先级越高
    },
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    sorter: {
      compare: (a: any, b: any) => a.userAccount.localeCompare(b.userAccount),
      multiple: 2,
    },
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    sorter: {
      compare: (a: any, b: any) => a.userName.localeCompare(b.userName),
      multiple: 1,
    },
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    width: 150, // 宽度
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    ellipsis: true, // 显示省略号
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    filters: [
      { text: '普通用户', value: 'user' },
      { text: 'VIP', value: 'vip' },
      { text: 'SVIP', value: 'svip' },
      { text: '管理员', value: 'admin' },
    ],
    // filterMultiple: false,
    onFilter: (value: string, record: any) => record.userRole.indexOf(value) === 0,
    sorter: (a: any, b: any) => a.userRole.localeCompare(b.userRole),
    sortDirections: ['descend', 'ascend'],
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    ellipsis: true, // 显示省略号
    sorter: {
      compare: (a: any, b: any) => (dayjs(a.createTime).isBefore(dayjs(b.createTime)) ? -1 : 1),
      multiple: 4,
    },
  },
  {
    title: '操作',
    key: 'action', // key 操作空间自定义
  },
]

// 定义数据
// ref 响应式变量 数组整体变化才会触发重新渲染
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

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
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

/**
 * 表格变化之后 重新获取数据
 * @param page 分页参数
 */
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

/**
 * 搜索数据
 */
const doSearch = () => {
  searchParams.current = 1 // 重置页码
  fetchData()
}

/**
 * 重置搜索条件
 */
const doReset = () => {
  searchParams.userAccount = ''
  searchParams.userName = ''
  searchParams.userRole = ''
  searchParams.id = undefined
  doSearch()
}

/**
 * 删除用户 (admin)
 * @param id 要删除的用户id
 */
const doDelete = async (id: number) => {
  if (!id) return
  const res = await deleteUserUsingPost({ id }) // 后端接口
  if (res.data.code === 0) {
    // openDoDelete.value = false
    message.success('删除成功')
    fetchData() // 刷新数据
  } else {
    message.error('删除失败 ' + res.data.message)
  }
}

/**
 * 编辑数据
 */
const editableData: UnwrapRef<Record<number, API.UserUpdateRequest>> = reactive({})
const doEdit = (id: number) => {
  editableData[id] = cloneDeep(dataList.value.filter((item) => id === item.id)[0])
}
const doSave = async (id: number) => {
  Object.assign(dataList.value.filter((item) => id === item.id)[0], editableData[id])
  await updateUserUsingPost({ ...editableData[id] }) // 后端接口
  message.success('保存成功')
  delete editableData[id] // 清空编辑
}
const doCancel = (id: number) => {
  delete editableData[id]
}
</script>

<style scoped>
/* 选择标签 <a-form-item> */
.ant-form-item {
  margin-top: 10px;
  margin-bottom: 10px;
}

.editable-row-operations button {
  margin-right: 8px;
  margin-top: 5px;
}
</style>

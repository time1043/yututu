<template>
  <div id="pictureManagePage">
    <!-- 上方 表单搜索栏 https://antdv.com/components/form-cn/ -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item>
        <a-input v-model:value="searchParams.id" placeholder="输入图片ID" allow-clear />
      </a-form-item>

      <a-form-item>
        <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" allow-clear />
      </a-form-item>

      <a-form-item>
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="从名称和简介搜索"
          allow-clear
        />
      </a-form-item>

      <a-form-item>
        <a-select
          v-model:value="searchParams.category"
          placeholder="选择分类"
          :options="categoryOptions"
          style="min-width: 197.43px"
          allow-clear
        />
      </a-form-item>

      <a-form-item>
        <a-select
          v-model:value="searchParams.tags"
          mode="tags"
          placeholder="输入标签"
          :options="tagOptions"
          style="min-width: 197.43px"
          allow-clear
        />
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
      :scroll="{ x: 1500 }"
    >
      <!-- 表格体 插槽 -->
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'url'">
          <a-image :src="record.url" :width="120" />
        </template>

        <template v-else-if="column.dataIndex === 'id'">
          <a-typography-paragraph :copyable="{ tooltip: false }">
            {{ record.id }}
          </a-typography-paragraph>
        </template>

        <template v-else-if="column.dataIndex === 'userId'">
          <a-typography-paragraph :copyable="{ tooltip: false }">
            {{ record.userId }}
          </a-typography-paragraph>
        </template>

        <template v-else-if="column.dataIndex === 'name'">
          {{ record.name.length > 40 ? record.name.slice(0, 40) + '...' : record.name }}
        </template>

        <template v-else-if="column.dataIndex === 'category'">
          <a-tag color="green">{{ record.category }}</a-tag>
        </template>

        <template v-else-if="column.dataIndex === 'tags'">
          <!-- <a-tag color="blue">{{ record.tags }}</a-tag> -->
          <!-- <a-space wrap> </a-space> -->
          <div v-if="record.tags === '[]'"></div>
          <a-tag
            v-for="tag in JSON.parse(record.tags || '[]')"
            color="blue"
            :key="tag"
            style="margin-top: 7px"
          >
            {{ tag }}
          </a-tag>
        </template>

        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>

        <template v-else-if="column.dataIndex === 'picInfo'">
          <div>格式：{{ record.picFormat }}</div>
          <div>宽度：{{ record.picWidth }}</div>
          <div>高度：{{ record.picHeight }}</div>
          <div>宽高比：{{ record.picScale }}</div>
          <div>大小：{{ (record.picSize / 1024).toFixed(2) }}KB</div>
        </template>

        <template v-else-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>

        <template v-else-if="column.key === 'action'">
          <!-- <a-button danger @click="doDelete(record.id)"> 删除 </a-button> -->
          <a-button danger>
            <a-popconfirm v-if="dataList.length" title="确定删除?" @confirm="doDelete(record.id)">
              <a> 删除 </a>
            </a-popconfirm>
          </a-button>

          <a-button primary>
            <a :href="`/add_picture?id=${record.id}`" target="_blank">编辑</a>
          </a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import {
  deletePictureUsingPost,
  listPictureByPageUsingPost,
  listPictureTagCategoryUsingGet,
} from '@/api/pictureController'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { computed, onMounted, reactive, ref } from 'vue'

// 页面加载时获取数据，请求一次
onMounted(() => {
  fetchData()
  getTagCategoryOpeions()
})

// 定义列名
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 90,
  },
  {
    title: '图片',
    dataIndex: 'url',
    width: 153,
  },
  {
    title: '名称',
    dataIndex: 'name',
    width: 160,
  },
  {
    title: '简介',
    dataIndex: 'introduction',
    width: 285,
    // ellipsis: true,
  },
  {
    title: '类型',
    dataIndex: 'category',
  },
  {
    title: '标签',
    dataIndex: 'tags',
  },
  {
    title: '图片信息',
    dataIndex: 'picInfo',
    width: 135,
  },
  {
    title: '用户 id',
    dataIndex: 'userId',
    width: 90,
  },
  // {
  //   title: '空间 id',
  //   dataIndex: 'spaceId',
  //   width: 80,
  // },
  {
    title: '审核信息',
    dataIndex: 'reviewMessage',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '操作',
    key: 'action',
    width: 167,
    // fixed: 'right',
  },
]

// 后端数据回显前端
const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

// 定义数据
// ref 响应式变量 数组整体变化才会触发重新渲染
const dataList = ref<API.Picture[]>([])
const total = ref<number>(0)

// 搜索条件
// reactive 响应式变量 对象中任何一个属性发生变化都会触发重新渲染
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'deascend',
  id: undefined,
  userId: undefined,
  searchText: undefined,
  tags: [],
  category: undefined,
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
  const res = await listPictureByPageUsingPost({ ...searchParams }) // 后端接口
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = Number(res.data.data.total) ?? 0
  } else message.error('获取数据失败 ' + res.data.message)
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
  searchParams.searchText = undefined
  searchParams.name = undefined
  searchParams.id = undefined
  searchParams.userId = undefined
  searchParams.tags = []
  searchParams.category = undefined
  doSearch()
}

/**
 * 删除图片 (admin)
 * @param id 要删除的图片id
 */
const doDelete = async (id: number) => {
  if (!id) return
  const res = await deletePictureUsingPost({ id }) // 后端接口
  if (res.data.code === 0) {
    // openDoDelete.value = false
    message.success('删除成功')
    fetchData() // 刷新数据
  } else message.error('删除失败 ' + res.data.message)
}

/**
 * 获取图片分类标签 (后端回显前端)
 */
const getTagCategoryOpeions = async () => {
  const res = await listPictureTagCategoryUsingGet() // 后端接口
  if (res.data.code === 0 && res.data.data) {
    // 数据格式有要求 https://antdv.com/components/select-cn/
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return { value: data, label: data }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return { value: data, label: data }
    })
  } else message.error('获取图片分类标签失败 ' + res.data.message)
}
</script>

<style scoped>
/* 选择标签 <a-form-item> */
.ant-form-item {
  margin-top: 10px;
  margin-bottom: 10px;
}

.ant-table-cell button {
  margin-right: 8px;
  margin-top: 5px;
}
</style>

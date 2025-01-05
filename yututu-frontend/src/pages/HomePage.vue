<template>
  <div id="homePage">
    <!-- 搜索条 -->
    <div class="searchBar">
      <!-- 搜索框 -->
      <!-- https://antdv.com/components/input-cn/ -->
      <a-input-search
        v-model:value="searchParams.searchText"
        placeholder="请输入搜索关键词"
        enter-button="搜索"
        size="large"
        @search="doSearch"
      />

      <!-- 分类和标签筛选 -->
      <!-- https://antdv.com/components/tabs-cn/ -->
      <a-tabs v-model:active-key="selectedCategory" @change="doSearch">
        <a-tab-pane key="all" tab="全部" />
        <a-tab-pane v-for="category in categoryOptions" :key="category" :tab="category" />
      </a-tabs>
      <!-- https://antdv.com/components/tag-cn/ -->
      <div class="tag-list">
        <a-space :size="[0, 8]" wrap>
          <a-checkable-tag
            v-for="(tag, index) in tagOptions"
            :key="tag"
            v-model:checked="selectedTagList[index]"
            @change="doSearch"
          >
            {{ tag }}
          </a-checkable-tag>
        </a-space>
      </div>
    </div>

    <!-- 图片列表展示 -->
    <!-- https://antdv.com/components/list-cn -->
    <div id="pictureList">
      <a-list
        :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
        :data-source="dataList"
        :pagination="pagination"
        :loading="loading"
      >
        <template #renderItem="{ item: picture }">
          <!-- 单张卡片 https://antdv.com/components/card-cn/ -->
          <a-list-item style="padding: 0">
            <a-card hoverable style="width: 260px" @click="doClickPicture(picture)">
              <template #cover>
                <img
                  :alt="picture.name"
                  :src="picture.url"
                  style="height: 180px; object-fit: contain"
                />
              </template>
              <a-card-meta :title="picture.name">
                <template #description>
                  <a-flex>
                    <a-tag color="green">{{ picture.category ?? '默认' }}</a-tag>
                    <a-tag color="blue" v-for="tag in picture.tags" :key="tag">
                      {{ tag }}
                    </a-tag>
                  </a-flex>
                </template>
              </a-card-meta>
            </a-card>
          </a-list-item>
        </template>
      </a-list>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController'
import { message } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

// 页面加载时获取数据，请求一次
onMounted(() => {
  getTagCategoryOpeions()
  fetchData()
})

const router = useRouter()

// 后端数据回显前端
const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])
// 用户选择的
const selectedCategory = ref<string>('all')
const selectedTagList = ref<boolean[]>([]) // 选中为true

// 定义数据
// ref 响应式变量 数组整体变化才会触发重新渲染
const dataList = ref<API.PictureVO[]>([])
const total = ref<number>(0)
const loading = ref(true)

// 搜索条件
// reactive 响应式变量 对象中任何一个属性发生变化都会触发重新渲染
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'deascend',
  tags: [],
  category: undefined,
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value,
    // showSizeChanger: true,
    onChange: (page: number, pageSize: number) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
      fetchData()
    },
  }
})

/**
 * 获取数据
 */
const fetchData = async () => {
  loading.value = true

  // 转换搜索参数
  const params = { ...searchParams, tags: [] as string[] }
  // 分类参数
  if (selectedCategory.value !== 'all') params.category = selectedCategory.value
  // 标签参数 [true, false, true] -> ['java', 'vue']
  selectedTagList.value.forEach((useTag, index) => {
    if (useTag) params.tags?.push(tagOptions.value[index])
  })

  const res = await listPictureVoByPageUsingPost(params) // 后端接口
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = Number(res.data.data.total) ?? 0
  } else message.error('获取数据失败 ' + res.data.message)
  loading.value = false
}

/**
 * 重新搜索
 */
const doSearch = () => {
  searchParams.current = 1 // 重置页码
  // searchParams.category = selectedCategory.value
  // searchParams.tags = selectedTagList.value
  //   .map((item, index) => (item ? tagOptions.value[index] : ''))
  //   .filter((item) => item)
  fetchData()
}

/**
 * 获取图片分类标签 (后端回显前端)
 */
const getTagCategoryOpeions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 后端回显前端
    tagOptions.value = res.data.data.tagList ?? []
    categoryOptions.value = res.data.data.categoryList ?? []
  } else message.error('获取图片分类标签失败 ' + res.data.message)
}

/**
 * 点击图片实现跳转
 * @param picture 图片
 */
const doClickPicture = (picture: API.PictureVO) => {
  // window.open(picture.url)
  router.push({ path: `/picture/${picture.id}` })
}
</script>

<style scoped>
#homePage {
  margin-bottom: 16px;
}

#homePage .searchBar {
  max-width: 480px;
  margin: 0 auto 16px;
}

#homePage .tar-bar {
  margin-bottom: 16px;
}
</style>

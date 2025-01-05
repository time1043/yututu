<template>
  <div id="pictureDetailPage">
    <!-- 图片详情：栅格布局 一行两列 https://antdv.com/components/grid-cn/ -->
    <a-row :gutter="[16, 16]">
      <!-- 图片预览 -->
      <a-col :sm="24" :md="16" :xl="18">
        <a-card title="图片预览">
          <a-image :src="picture.url" style="max-height: 600; object-fit: contain" />
        </a-card>
      </a-col>
      <!-- 图片信息 -->
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :size="24" :src="picture.user?.userAvatar" />
                <div>{{ picture.user?.userName }}</div>
              </a-space>
            </a-descriptions-item>

            <a-descriptions-item label="名称">
              {{ picture.name ?? '未命名' }}
            </a-descriptions-item>

            <a-descriptions-item label="简介">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>

            <a-descriptions-item label="分类">
              <a-tag color="green">{{ picture.category ?? '默认' }}</a-tag>
            </a-descriptions-item>

            <a-descriptions-item label="标签">
              <a-tag color="blue" v-for="tag in picture.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>

            <a-descriptions-item label="格式">
              {{ picture.picFormat ?? '-' }}
            </a-descriptions-item>

            <a-descriptions-item label="宽度">
              {{ picture.picWidth ?? '-' }}
            </a-descriptions-item>

            <a-descriptions-item label="高度">
              {{ picture.picHeight ?? '-' }}
            </a-descriptions-item>

            <a-descriptions-item label="宽高比">
              {{ picture.picScale ?? '-' }}
            </a-descriptions-item>

            <a-descriptions-item label="大小">
              {{ formatSize(picture.picSize) }}
            </a-descriptions-item>
          </a-descriptions>

          <!-- 操作按钮 -->
          <a-space wrap>
            <a-button type="primary" @click="doDownload">
              免费下载 <template #icon><DownloadOutlined /></template>
            </a-button>

            <a-button danger :icon="h(DeleteOutlined)" v-if="canEdit">
              <a-popconfirm v-if="picture" title="确定删除?" @confirm="doDelete(picture.id)">
                <a> 删除 </a>
              </a-popconfirm>
            </a-button>

            <a-button primary :icon="h(EditOutlined)" v-if="canEdit">
              <a :href="`/add_picture?id=${picture.id}`" target="_blank">编辑</a>
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { deletePictureUsingPost, getPictureVoByIdUsingGet } from '@/api/pictureController'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { downloadImage, formatSize } from '@/utils'
import { DeleteOutlined, DownloadOutlined, EditOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { computed, h, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 当前用户信息
const loginUserStore = useLoginUserStore()

const router = useRouter() // 路由跳转
const route = useRoute() // 获取信息

// 首次进入页面加载
onMounted(() => {
  fetchPictureDetail()
})

// 接受动态路由参数
interface Props {
  id: number | undefined
}
const props = defineProps<Props>()
const picture = ref<API.PictureVO>({})

/**
 * 获取老数据 【新增页面 修改页面 复用】
 */
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({ id: props.id })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else message.error(res.data.message)
  } catch (e: any) {
    message.error('获取图片详情失败 ' + e.message)
  }
}

/**
 * 是否具有权限 (否则不显示)
 */
const canEdit = computed(() => {
  // 未登录无权限操作
  const loginUser = loginUserStore.loginUser // 当前用户
  if (!loginUser.id) return false
  // 只有管理员和作者能操作
  const pictureUser = picture.value.user || {} // 图片拥有者
  return loginUser.id === pictureUser.id || loginUser.userRole === 'admin'
})

/**
 * 删除图片
 * @param id 图片id
 */
const doDelete = async (id: number | undefined) => {
  const res = await deletePictureUsingPost({ id: id })
  if (res.data.code === 0) {
    message.success('删除成功')
    router.push('/')
  } else message.error(res.data.message)
}

/**
 * 下载图片
 */
const doDownload = async () => {
  downloadImage(picture.value.url, picture.value.name)
}
</script>

<style scoped></style>

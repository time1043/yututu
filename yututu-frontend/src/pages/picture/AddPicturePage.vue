<template>
  <div id="addPicturePage">
    <!-- 标题 -->
    <h2 style="margin-bottom: 16px">{{ route.query?.id ? '修改图片' : '创建图片' }}</h2>

    <!-- 图片上传组件 -->
    <pictureUpload :picture="picture" :onSuccess="onSuccess" />

    <!-- 图片信息表单 https://antdv.com/components/form-cn/ -->
    <a-form
      v-if="picture"
      name="basic"
      layout="vertical"
      :model="pictureForm"
      @finish="handleSubmit"
    >
      <a-form-item name="name" label="图片名称">
        <a-input v-model:value="pictureForm.name" placeholder="输入图片名称" allow-clear />
      </a-form-item>

      <a-form-item name="introduction" label="图片简介">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="输入图片简介"
          allow-clear
          :autoSize="{ minRows: 7, maxRows: 7 }"
        />
      </a-form-item>

      <a-form-item name="category" label="图片分类">
        <a-auto-complete
          v-model:value="pictureForm.category"
          placeholder="输入图片分类"
          allow-clear
          :options="categoryOptions"
        />
      </a-form-item>

      <a-form-item name="tags" label="图片标签">
        <a-select
          v-model:value="pictureForm.tags"
          mode="tags"
          placeholder="输入图片标签"
          allow-clear
          :options="tagOptions"
        />
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">提交</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import {
  editPictureUsingPost,
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet,
} from '@/api/pictureController'
import PictureUpload from '@/components/PictureUpload.vue'
import { message } from 'ant-design-vue'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter() // 路由跳转
const route = useRoute() // 获取信息

// 前端数据
const picture = ref<API.PictureVO>()
const pictureForm = reactive<API.PictureEditRequest>({})

// 后端数据回显前端
const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

// 首次进入页面加载
onMounted(() => {
  getTagCategoryOpeions()
  getOldPicture()
})

/**
 * 图片上传成功回调
 * @param newPicture
 */
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

/**
 * 图片修改表单提交
 * @param values
 */
const handleSubmit = async (values: any) => {
  // 获取图片id
  const pictureId = picture.value?.id
  if (!pictureId) return

  const res = await editPictureUsingPost({ id: pictureId, ...values }) // 后端接口

  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success('修改成功')
    router.push({ path: `/picture/${pictureId}`, replace: true }) // 跳转到图片详情页
  } else message.error('修改失败 ' + res.data.message)
}

/**
 * 获取图片分类标签 (后端回显前端)
 */
const getTagCategoryOpeions = async () => {
  const res = await listPictureTagCategoryUsingGet()
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

/**
 * 获取老数据 【新增页面 修改页面 复用】
 */
const getOldPicture = async () => {
  const pictureId = route.query.id // http://localhost:5011/add_picture?id=1873651749226729473
  if (!pictureId) return
  const res = await getPictureVoByIdUsingGet({ id: pictureId })
  if (res.data.code === 0 && res.data.data) {
    picture.value = res.data.data
    pictureForm.name = res.data.data.name
    pictureForm.introduction = res.data.data.introduction
    pictureForm.category = res.data.data.category
    pictureForm.tags = res.data.data.tags
  } else {
  }
}
</script>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>

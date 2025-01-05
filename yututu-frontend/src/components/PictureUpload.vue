<template>
  <!-- https://antdv.com/components/upload-cn/ -->
  <div id="pictureUpload">
    <a-upload
      list-type="picture-card"
      class="avatar-uploader"
      :show-upload-list="false"
      :custom-request="handleUpload"
      :before-upload="beforeUpload"
    >
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
      <div v-else>
        <loading-outlined v-if="loading"></loading-outlined>
        <plus-outlined v-else></plus-outlined>
        <div class="ant-upload-text">点击或拖拽</div>
      </div>
    </a-upload>
  </div>
</template>

<script lang="ts" setup>
import { uploadPictureUsingPost } from '@/api/pictureController'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons-vue'
import type { UploadProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { ref } from 'vue'

// 父子通信
interface Props {
  picture?: API.PictureVO
  onSuccess: (picture: API.PictureVO) => void
}
const props = defineProps<Props>()

const loading = ref<boolean>(false)

/**
 * 文件上传前的校验 (前端校验降低后端服务器的压力)
 * @param file
 */
const beforeUpload = (file: UploadProps['fileList'][number]) => {
  // 图片格式
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) message.error('不支持该格式的文件, 只能上传JPG/PNG文件!')

  // 图片大小
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) message.error('图片大小不能超过2MB!')

  return isJpgOrPng && isLt2M
}

/**
 * 上传图片 调用后端接口
 * @param file 上传的文件
 */
const handleUpload = async ({ file }: any) => {
  loading.value = true // 开启loading

  try {
    const params = props.picture ? { id: props.picture.id } : {}
    const res = await uploadPictureUsingPost(params, {}, file) // 后端接口
    if (res.data.code === 0 && res.data.data) {
      message.success('上传成功')
      props.onSuccess?.(res.data.data) // 将上传成功的信息传给父组件
    } else message.error('上传失败 ' + res.data.message)
  } catch (error) {
    console.log('图片上传失败 ', error)
    message.error('图片上传失败 ', error.message)
  }

  loading.value = false // 上传成功后关闭loading
}
</script>

<style scoped>
#pictureUpload :deep(.ant-upload) {
  width: 100% !important;
  height: 100% !important;
  min-width: 152px;
  min-height: 152px;
}

#pictureUpload img {
  max-width: 100%;
  max-height: 480px;
}

.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
</style>

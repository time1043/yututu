// npm run openapi
// https://www.npmjs.com/package/@umijs/openapi
import { generateService } from '@umijs/openapi'

const baseURL = 'http://127.0.0.1:8011'
generateService({
  requestLibPath: "import request from '@/common/request'", // 请求库
  schemaPath: `${baseURL}/api/v2/api-docs?group=default`, // 文档请求接口
  serversPath: './src', // 生成src/api
})

// import { healthUsingGet } from '@/api/mainController'
// await healthUsingGet().then((res) => {
//   console.log(res.data?.data)
// })

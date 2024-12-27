// https://www.axios-http.cn/docs/instance
// https://www.axios-http.cn/docs/interceptors
import axios from 'axios'
import { message } from 'ant-design-vue'
import Config from './config'

const { BACKEND_URL: baseURL } = Config
const myAxios = axios.create({
  baseURL,
  timeout: 60000, // 60s=6wms
  withCredentials: true, // 允许跨域携带cookie
})

// 添加请求拦截器
myAxios.interceptors.request.use(
  // 在发送请求之前做些什么
  function (config) {
    return config
  },
  // 对请求错误做些什么
  function (error) {
    return Promise.reject(error)
  },
)

// 添加响应拦截器
myAxios.interceptors.response.use(
  // 2xx 范围内的状态码都会触发该函数
  // 对响应数据做点什么
  function (response) {
    const { data } = response
    // 未登录40100
    // 未登录，跳转到登录页面
    // 未登录，但不用跳转：已经在登陆页面、当前请求接口不用用户信息
    if (data.code === 40100) {
      if (
        !response.request.responseURL.includes('user/get/login') &&
        !window.location.pathname.includes('user/login') &&
        !window.location.pathname.includes('user/register')
      ) {
        message.warning('未登录，请先登录')
        window.location.href = `/user/login?redirect=${window.location.href}`
      }
    }
    return response
  },
  // 超出 2xx 范围的状态码都会触发该函数
  // 对响应错误做点什么
  function (error) {
    return Promise.reject(error)
  },
)

export default myAxios

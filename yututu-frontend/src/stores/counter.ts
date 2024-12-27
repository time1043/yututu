import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

/**
 * 状态管理
 * 一个状态存储一类要共享的数据
 *
 * 1 定义状态的初始值
 * 2 定义修改状态的方法
 */
export const useCounterStore = defineStore('counter', () => {
  const count = ref(0)
  const doubleCount = computed(() => count.value * 2)

  function increment() {
    count.value++
  }

  return { count, doubleCount, increment }
})

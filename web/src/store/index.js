import { createStore } from 'vuex'
import ModuleUser from './user'

export default createStore({
  state: {},
  getters: {},
  mutations: {},
  actions: {},
  modules: {    // 在此处导入模块
    user: ModuleUser
  },
})
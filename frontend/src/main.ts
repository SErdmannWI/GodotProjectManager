import { createApp } from 'vue'
import App from './App.vue'
import { FontAwesomeIcon } from './plugins/font-awesome'
import router from './router'
import store from './store'

createApp(App)
  .component('font-awesome-icon', FontAwesomeIcon)
  .use(store)
  .use(router)
  .mount('#app')

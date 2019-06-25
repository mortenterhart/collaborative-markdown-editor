import Vue from 'vue';
import './plugins/vuetify';
import './plugins/snotify';
import './plugins/axios';
import './plugins/chat';
import store from './store';
import App from './App.vue';
import router from './router';

Vue.prototype.location = window.location;

Vue.config.productionTip = false;

new Vue({
    render: h => h(App),
    store,
    router,
}).$mount('#app');

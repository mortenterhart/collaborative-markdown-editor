import Vue from 'vue';
import './plugins/vuetify';
import './plugins/snotify';
import './plugins/axios';
import store from './store';
import App from './App.vue';

Vue.config.productionTip = false;

new Vue({
    render: h => h(App),
    store,
}).$mount('#app');

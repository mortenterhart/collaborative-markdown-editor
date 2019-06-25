import Vue from 'vue';
import axios from 'axios';
import VueAxios from 'vue-axios';

Vue.use(VueAxios, axios.create({
    baseURL: location.origin + location.pathname.replace(/\/?$/, "") + '/api'
}));

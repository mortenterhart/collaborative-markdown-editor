import Vue from 'vue'
import Vuex from 'vuex'
import login from './modules/login'
import app from './modules/app'

Vue.use(Vuex);

const debug = process.env.NODE_ENV !== 'production';

export default new Vuex.Store({
    modules: {
        login,
        app
    },
    strict: debug,
})

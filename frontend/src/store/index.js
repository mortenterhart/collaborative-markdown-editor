import Vue from 'vue';
import Vuex from 'vuex';
import login from './modules/login';
import app from './modules/app';
import createPersistedState from 'vuex-persistedstate';

Vue.use(Vuex);

const debug = process.env.NODE_ENV !== 'production';

export default new Vuex.Store({
    modules: {
        login,
        app
    },
    strict: debug,
    plugins: [createPersistedState({
        paths: ['login', 'app.currentDocument', 'app.title']
    })],
});

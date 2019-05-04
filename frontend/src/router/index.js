import Vue from 'vue'
import VueRouter from 'vue-router'
import Welcome from '../components/Welcome'
import Document from '../components/Document'
import ErrorPage from '../components/ErrorPage'

Vue.use(VueRouter);

const routes = [
    { path: '/', component: Welcome },
    { path: '/doc/:id', component: Document },
    { path: '*', component: ErrorPage }
];

export default new VueRouter({
    base: '/CMD/',
    routes
})
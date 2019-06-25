import Vue from 'vue';
import VueRouter from 'vue-router';
import Welcome from '../components/Welcome';
import Document from '../components/Document';
import ErrorPage from '../components/ErrorPage';
import ForbiddenPage from '../components/ForbiddenPage';

Vue.use(VueRouter);

const routes = [
    { path: '/', component: Welcome },
    { path: '/doc/:id', component: Document, name: 'document' },
    { path: '/Forbidden', component: ForbiddenPage },
    { path: '*', component: ErrorPage }
];

export default new VueRouter({
    base: location.pathname,
    routes
});

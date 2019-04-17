import Vue from 'vue';
import Snotify from 'vue-snotify';
import 'vue-snotify/styles/material.css';

Vue.use(Snotify, {
    toast: {
        timeout: 4000 // default duration
    }
});
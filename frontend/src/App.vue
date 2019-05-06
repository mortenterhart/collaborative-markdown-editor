<template>
    <v-app style="max-height: 100vh;">
        <v-navigation-drawer
                v-if="this.$store.state.login.isLoggedIn"
                v-model="drawer"
                fixed
                app
            >
            <Drawer/>
        </v-navigation-drawer>
        <v-toolbar
                fixed
                app
        >
            <v-toolbar-side-icon
                    v-if="this.$store.state.login.isLoggedIn"
                    @click="drawer = !drawer"/>
            <v-toolbar-title v-text="title"/>
            <v-spacer/>
            <v-toolbar-items>
                <template v-if="!this.$store.state.login.isLoggedIn">
                    <v-btn flat @click="$store.commit('login/showLoginDialog')">Login</v-btn>
                </template>
                <template v-else>
                    <v-btn flat @click="handleLogout">Logout</v-btn>
                </template>
            </v-toolbar-items>
        </v-toolbar>
        <v-content>
            <Login/>
            <!-- route outlet -->
            <!-- component matched by the route will render here -->
            <router-view></router-view>
        </v-content>
        <vue-snotify></vue-snotify>
    </v-app>
</template>

<script>
    import Login from './components/Login'
    import Drawer from "./components/Drawer";
    import Cookies from 'js-cookie';

    export default {
        name: 'App',
        components: {
            Drawer,
            Login
        },
        data() {
            return {
                drawer: false,
                title: 'Collaborative Markdown Editor'
            }
        },
        methods: {
            handleLogout: function() {
                this.axios.post('authentication/logout', {},
                    {
                        withCredentials: true
                    }
                );
                this.$store.commit('login/setIsLoggedIn', false);
                this.$snotify.success(
                    'You\'re getting logged out',
                    'Success'
                );
            }
        }
    }
</script>

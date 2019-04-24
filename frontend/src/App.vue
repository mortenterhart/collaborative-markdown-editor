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
            <template v-if="false">
                <Welcome />
            </template>
            <template v-else>
                <v-container fluid fill-height>
                    <v-layout row>
                        <v-flex xs6 pr-2>
                            <MDE @contentWasChanged="content = $event"/>
                        </v-flex>
                        <v-flex xs6 pl-2>
                            <Preview :content="content"/>
                        </v-flex>
                    </v-layout>
                </v-container>
            </template>
        </v-content>
        <vue-snotify></vue-snotify>
    </v-app>
</template>

<script>
    import Welcome from './components/Welcome'
    import MDE from './components/Editor'
    import Preview from './components/Preview'
    import Login from './components/Login'
    import Drawer from "./components/Drawer";
    import Cookies from 'js-cookie';

    export default {
        name: 'App',
        components: {
            Welcome,
            Drawer,
            MDE,
            Preview,
            Login
        },
        data() {
            return {
                content: '',
                drawer: false,
                items: [
                    {
                        icon: 'apps',
                        title: 'Welcome',
                        to: '/'
                    },
                    {
                        icon: 'bubble_chart',
                        title: 'Inspire',
                        to: '/inspire'
                    }
                ],
                title: 'Collaborative Markdown Editor'
            }
        },
        methods: {
            handleLogout: function() {
                Cookies.set('JSESSIONID', '=', { path: '' });
                Cookies.remove('JSESSIONID', { path: '' });
                this.$store.commit('login/setIsLoggedIn', false);
                this.$snotify.success(
                    'You\'re getting logged out',
                    'Success'
                );
            }
        }
    }
</script>

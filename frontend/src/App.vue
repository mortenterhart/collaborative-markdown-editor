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
            <v-toolbar-title v-text="this.$store.state.app.title"/>
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

    export default {
        name: 'App',
        components: {
            Drawer,
            Login
        },
        data() {
            return {
                drawer: false
            }
        },
        methods: {
            handleLogout: function() {
                this.axios.post('authentication/logout', {},
                    {
                        withCredentials: true
                    }
                ).then((response) => {
                    this.$router.push('/');
                    this.$store.commit('login/setIsLoggedIn', false);
                    this.$snotify.success(
                        'You\'re getting logged out',
                        'Success'
                    );
                }).catch((error) => {
                    this.$snotify.error(
                        error.response.data.message,
                        'Error'
                    );
                })
            }
        }
    }
</script>

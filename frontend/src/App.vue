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
            <img src="./assets/cmd_logo.png" alt="CMD Logo" width="45"/>
            <v-toolbar-title v-text="this.$store.state.app.title"/>
            <template v-if="$route.name === 'document'">
                <template
                        v-if="this.$store.state.app.currentDocument.repo.owner.id === this.$store.state.login.user.id">
                    <v-subheader>owned by you</v-subheader>
                </template>
                <template v-else>
                    <v-subheader>{{ ' owned by ' + this.$store.state.app.currentDocument.repo.owner.name }}
                    </v-subheader>
                </template>
            </template>
            <v-spacer/>
            <template v-if="$route.name === 'document' && this.$store.state.app.otherCollaborators.length !== 0">
                <v-menu offset-y>
                    <template v-slot:activator="{ on }">
                        <v-btn
                                color="primary"
                                dark
                                v-on="on"
                        >
                            Other Collaborators
                        </v-btn>
                    </template>
                    <v-list>
                        <v-list-tile
                                v-for="(item, index) in this.$store.state.app.otherCollaborators"
                                :key="index"
                        >
                            <v-list-tile-title>{{ item.name }}</v-list-tile-title>
                        </v-list-tile>
                    </v-list>
                </v-menu>
            </template>
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
    import Login from './components/Login';
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
                );
                this.$router.push('/');
                this.$store.commit('login/setIsLoggedIn', false);
                this.$snotify.success(
                    'You\'re getting logged out',
                    'Success'
                );
            }
        }
    }
</script>

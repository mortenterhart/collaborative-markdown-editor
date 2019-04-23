<template>
    <v-layout row justify-center>
        <v-dialog v-model="dialog" max-width="600px" lazy>
            <v-card>
                <v-card-text>
                    <v-tabs grow>
                        <v-tab>Login</v-tab>
                        <v-tab>Register</v-tab>
                        <v-tab-item>
                            <v-form>
                                <v-text-field
                                        append-icon="person"
                                        name="username"
                                        label="Username"
                                        type="text"
                                        v-model="loginUsername"></v-text-field>
                                <v-text-field
                                        :type="hideLoginPassword ? 'password' : 'text'"
                                        :append-icon="hideLoginPassword ? 'visibility_off' : 'visibility'"
                                        name="password"
                                        label="Password"
                                        v-model="loginPassword"
                                        @click:append="hideLoginPassword = !hideLoginPassword"></v-text-field>
                                <v-btn block color="primary" @click="handleLogin">Login</v-btn>
                            </v-form>
                        </v-tab-item>
                        <v-tab-item>
                            <v-form>
                                <v-text-field
                                        append-icon="person"
                                        :rules="usernameRules"
                                        name="username"
                                        label="Username"
                                        type="text"
                                        v-model="regUsername"></v-text-field>
                                <v-text-field
                                        append-icon="email"
                                        :rules="emailRules"
                                        name="email"
                                        label="Email"
                                        type="email"
                                        v-model="regEmail"></v-text-field>
                                <v-text-field
                                        :type="hideRegPassword ? 'password' : 'text'"
                                        :append-icon="hideRegPassword ? 'visibility_off' : 'visibility'"
                                        :rules="passwordRules"
                                        name="password"
                                        label="Password"
                                        v-model="regPassword"
                                        @click:append="hideRegPassword = !hideRegPassword"></v-text-field>
                                <v-btn block color="primary" @click="handleRegistration">Register</v-btn>
                            </v-form>
                        </v-tab-item>
                    </v-tabs>
                </v-card-text>
            </v-card>
        </v-dialog>
    </v-layout>
</template>

<script>
    import { mapMutations } from 'vuex';
    import Cookie from "js-cookie";

    export default {
        name: "Login",
        data: function() {
            return {
                loginUsername: '',
                loginPassword: '',
                regUsername: '',
                usernameRules: [],
                regEmail: '',
                emailRules: [],
                regPassword: '',
                passwordRules: [],
                hideLoginPassword: true,
                hideRegPassword: true,
            }
        },
        methods: {
            ...mapMutations({
                hideLoginDialog: 'login/hideLoginDialog'
            }),
            handleLogin: function() {
                if (this.loginUsername.trim() === '') {
                    this.$snotify.error(
                        'Enter your username',
                        'Error'
                    );
                    return;
                }

                if (this.loginPassword.trim() === '') {
                    this.$snotify.error(
                        'Enter your password',
                        'Error'
                    );
                    return;
                }

                this.axios.post('/authentication/login',
                    {
                        username: this.loginUsername,
                        password: this.loginPassword
                    },
                    {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(() => {
                        this.hideLoginDialog();
                        this.$snotify.success(
                            'You\'re getting logged in',
                            'Success'
                        );
                        this.$store.commit('login/setIsLoggedIn', true)
                    }).catch((error) => {
                        this.$snotify.error(
                            error.response.data.message,
                            'Error'
                        );
                    }
                );

                /*
                this.axios.post('http://localhost:8080/CMD/api/authentication/login',
                    {
                        headers: {
                            Cookie: Cookie.get("JSESSIONID")
                        },
                        data: {

                        }
                    }).then((response) => {
                        console.log(response.data);
                        this.hideLoginDialog();
                        this.$snotify.success(
                            'You\'re getting logged in',
                            'Success'
                        );
                });*/
            },
            handleRegistration: function() {
                if (this.regUsername.trim() === '') {
                    this.$snotify.error(
                        'Enter a username',
                        'Error'
                    );
                    return;
                }

                if (this.regEmail.trim() === '') {
                    this.$snotify.error(
                        'Enter an email',
                        'Error'
                    );
                    return;
                }

                if (this.regPassword.trim() === '') {
                    this.$snotify.error(
                        'Enter a password',
                        'Error'
                    );
                    return;
                }

                this.axios.post('/authentication/register',
                    {
                        username: this.regUsername,
                        email: this.regEmail,
                        password: this.regPassword
                    },
                    {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(() => {
                        this.hideLoginDialog();
                        this.$snotify.success(
                            'You\'ve created an account',
                            'Success'
                        );

                        this.loginUsername = this.regUsername;
                        this.loginPassword = this.regPassword;
                        this.handleLogin();
                    }).catch((error) => {
                        this.$snotify.error(
                            error.response.data.message,
                            'Error'
                        );
                });
            }
        },
        watch: {
            regUsername: function(username) {
                if (username === '') {
                    this.usernameRules = []
                } else if (username.length < 4) {
                    this.usernameRules = ['Username requires at least 4 chars']
                } else if (username !== '') {
                    this.usernameRules = [ v => (v.match(/^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$/)) || 'Invalid username']
                }
            },
            regEmail: function(mail) {
                if (mail !== '') {
                    this.emailRules = [ v => (v.match(/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/)) || 'Invalid email address']
                } else if (mail === '') {
                    this.emailRules = []
                }
            },
            regPassword: function(password) {
                if (password === '') {
                    this.passwordRules = [];
                    return
                }

                if (!/\d/.test(password)) {
                    this.passwordRules = ['Password must contain at least one digit']
                } else if (!/[a-z]/.test(password)) {
                    this.passwordRules = ['Password must contain at least one lowercase char']
                } else if (!/[A-Z]/.test(password)) {
                    this.passwordRules = ['Password must contain at least one uppercase char']
                } else if (password.length < 8) {
                    this.passwordRules = ['Password requires at least 8 chars']
                } else {
                    this.passwordRules = []
                }
            }
        },
        computed: {
            dialog : {
                get: function() {
                    return this.$store.state.login.showLoginDialog
                },
                set: function(val) {
                    if (val) {
                        this.$store.commit('login/showLoginDialog')
                    } else {
                        this.$store.commit('login/hideLoginDialog')
                    }
                }
            }
        }
    }
</script>

<style scoped>

</style>

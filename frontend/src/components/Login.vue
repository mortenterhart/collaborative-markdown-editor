<template>
    <v-layout row justify-center>
        <v-dialog v-model="dialog" max-width="600px" lazy>
            <v-card>
                <v-card-text>
                    <v-tabs grow v-model="dialogTabs">
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
                                        :rules="[rules.required, rules.username, rules.usernameLength]"
                                        name="username"
                                        label="Username"
                                        type="text"
                                        v-model="regUsername"></v-text-field>
                                <v-text-field
                                        append-icon="email"
                                        :rules="[rules.required, rules.email]"
                                        name="email"
                                        label="Email"
                                        type="email"
                                        v-model="regEmail"></v-text-field>
                                <v-text-field
                                        :type="hideRegPassword ? 'password' : 'text'"
                                        :append-icon="hideRegPassword ? 'visibility_off' : 'visibility'"
                                        :rules="[rules.required, rules.passwordLength, rules.passwordLowerChar, rules.passwordOneDigit, rules.passwordUpperChar]"
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
    import {mapMutations} from 'vuex';

    export default {
        name: "Login",
        data: function() {
            return {
                dialogTabs: null,
                loginUsername: '',
                loginPassword: '',
                regUsername: '',
                regEmail: '',
                regPassword: '',
                hideLoginPassword: true,
                hideRegPassword: true,
                rules: {
                    required: value => !!value || 'This field is required',
                    usernameLength: value => value.length >= 4 || 'Username requires at least 4 characters',
                    username: value => {
                        const pattern = /^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$/;
                        return pattern.test(value) || 'Invalid username';
                    },
                    email: value => {
                        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                        return pattern.test(value) || 'Invalid email syntax';
                    },
                    passwordOneDigit: value => {
                        const pattern = /\d/;
                        return pattern.test(value) || 'Password must contain at least one digit';
                    },
                    passwordLowerChar: value => {
                        const pattern = /[a-z]/;
                        return pattern.test(value) || 'Password must contain at least one lowercase character';
                    },
                    passwordUpperChar: value => {
                        const pattern = /[A-Z]/;
                        return pattern.test(value) || 'Password must contain at least one uppercase character';
                    },
                    passwordLength: value => value.length >= 8 || 'Password requires at least 8 characters'
                }
            }
        },
        methods: {
            ...mapMutations({
                hideLoginDialog: 'login/hideLoginDialog'
            }),
            handleLogin: function() {
                if (this.loginUsername.trim() === '') {
                    this.$snotify.error(
                        'Enter a username',
                        'Error'
                    );
                    return;
                }

                if (this.loginPassword.trim() === '') {
                    this.$snotify.error(
                        'Enter a password',
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
                            'Content-Type': 'application/json',
                        }
                    }).then((response) => {
                    this.hideLoginDialog();
                    this.$snotify.success(
                        'You\'re getting logged in',
                        'Success'
                    );
                    this.$store.commit('login/setIsLoggedIn', true);
                    this.$store.commit('login/setUser', response.data.user);
                }).catch((error) => {
                        this.$snotify.error(
                            error.response.data.message,
                            'Error'
                        );
                    }
                );
            },
            handleRegistration: function() {
                if (this.regUsername.trim().length < 4) {
                    this.$snotify.error(
                        'Username requires at least 4 characters',
                        'Error'
                    );
                    return;
                }

                let pattern = /^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$/;
                if (!pattern.test(this.regUsername)) {
                    this.$snotify.error(
                        'Invalid username',
                        'Error'
                    );
                    return;
                }

                pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                if (!pattern.test(this.regEmail)) {
                    this.$snotify.error(
                        'Invalid email syntax',
                        'Error'
                    );
                    return;
                }

                if (this.regPassword.trim().length < 8) {
                    this.$snotify.error(
                        'Password requires at least 8 characters',
                        'Error'
                    );
                    return;
                }

                pattern = /\d/;
                if (!pattern.test(this.regPassword)) {
                    this.$snotify.error(
                        'Password must contain at least one digit',
                        'Error'
                    );
                    return;
                }

                pattern = /[a-z]/;
                if (!pattern.test(this.regPassword)) {
                    this.$snotify.error(
                        'Password must contain at least one lowercase character',
                        'Error'
                    );
                    return;
                }

                pattern = /[A-Z]/;
                if (!pattern.test(this.regPassword)) {
                    this.$snotify.error(
                        'Password must contain at least one uppercase character',
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
        computed: {
            dialog: {
                get: function() {
                    return this.$store.state.login.showLoginDialog;
                },
                set: function(val) {
                    if (val) {
                        this.$store.commit('login/showLoginDialog');
                    } else {
                        this.$store.commit('login/hideLoginDialog');
                    }
                }
            }
        },
        created() {
            let vm = this;
            window.addEventListener('keyup', function(e) {
                if (!vm.dialog || e.keyCode !== 13) {
                    return;
                }

                if (vm.dialogTabs === 0) {
                    vm.handleLogin();
                } else {
                    vm.handleRegistration();
                }
            })
        }
    };
</script>

<style scoped>

</style>

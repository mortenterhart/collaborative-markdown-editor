<template>
    <div>
        <section>
            <v-parallax :src="imageLink.sub_main" height="600">
                <v-layout column align-center justify-center class="white--text">
                    <h1 class="white--text mb-2 display-1 text-xs-center"
                        style="font-weight: 900; text-shadow: 3px 2px #000000">The Markdown Editor you share with your friends</h1>
                    <div class="white--text subheading mb-3 text-xs-center"
                         style="font-weight: 900; text-shadow: 2px 2px #000000">Unleash your creativity without limitations
                    </div>
                    <template v-if="!this.$store.state.login.isLoggedIn">
                        <v-btn class="blue lighten-2 mt-5" dark large @click="$store.commit('login/showLoginDialog')">
                            Get Started
                        </v-btn>
                    </template>
                </v-layout>
            </v-parallax>
        </section>

        <section>
            <v-layout column wrap class="my-5" align-center>
                <v-flex xs12 sm4 class="my-3">
                    <div class="text-xs-center">
                        <h2 class="headline">The best way to share your amazing writing</h2>
                        <span class="subheading">
                            No more restrictions, no more limits
                        </span>
                    </div>
                </v-flex>
                <v-flex xs12>
                    <v-container grid-list-xl>
                        <v-layout row wrap align-center>
                            <v-flex xs12 md4>
                                <v-card class="elevation-0 transparent">
                                    <v-card-text class="text-xs-center">
                                        <v-icon x-large class="blue--text text--lighten-2">public</v-icon>
                                    </v-card-text>
                                    <v-card-title primary-title class="layout justify-center">
                                        <div class="headline text-xs-center">Reach the world</div>
                                    </v-card-title>
                                    <v-card-text>
                                        Show your writing to all your friends not only to your mum or your friends. We
                                        love sharing great content. In this moment our editor is used by scientists all
                                        over the world who are not famous but that want to share their works to the
                                        world.
                                    </v-card-text>
                                </v-card>
                            </v-flex>
                            <v-flex xs12 md4>
                                <v-card class="elevation-0 transparent">
                                    <v-card-text class="text-xs-center">
                                        <v-icon x-large class="blue--text text--lighten-2">flash_on</v-icon>
                                    </v-card-text>
                                    <v-card-title primary-title class="layout justify-center">
                                        <div class="headline">Instant feedback</div>
                                    </v-card-title>
                                    <v-card-text>
                                        Time is important, we don't want you to waste it. Here you can preview your
                                        document in real time. And if you've made a mistake, you can easily revert your
                                        document to an earlier state and continue writing up a new idea.
                                    </v-card-text>
                                </v-card>
                            </v-flex>
                            <v-flex xs12 md4>
                                <v-card class="elevation-0 transparent">
                                    <v-card-text class="text-xs-center">
                                        <v-icon x-large class="blue--text text--lighten-2">share</v-icon>
                                    </v-card-text>
                                    <v-card-title primary-title class="layout justify-center">
                                        <div class="headline text-xs-center">Collaborate</div>
                                    </v-card-title>
                                    <v-card-text>
                                        Imagine working simultaneously on the same markdown document with multiple
                                        users. Don't you think it would be easier to get shit done? Our editor is both
                                        local and global and will help you to connect without limitations with people
                                        from your city, your state and your universe!
                                    </v-card-text>
                                </v-card>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </v-flex>
            </v-layout>
        </section>

        <section>
            <v-parallax :src="imageLink.social_cover" height="380">
                <v-layout column align-center justify-center>
                    <div class="headline white--text mb-3 text-xs-center">Stay tuned for exciting new features you
                        didn't knew you wanted
                    </div>
                </v-layout>
            </v-parallax>
        </section>

        <section>
            <v-container grid-list-xl>
                <v-layout row wrap justify-center class="my-5">
                    <v-flex xs12 sm4>
                        <v-card class="elevation-0 transparent">
                            <v-card-title primary-title class="layout justify-center">
                                <div class="headline">Company info</div>
                            </v-card-title>
                            <v-card-text>
                                We are not a company. We hate companies. Just imagine us like the guys from the Silicon
                                Valley series.
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs12 sm4 offset-sm1>
                        <v-card class="elevation-0 transparent">
                            <v-card-title primary-title class="layout justify-center">
                                <div class="headline">We are hiring</div>
                            </v-card-title>
                            <v-card-text>
                                Are you a creative person? Do you like techy stuff? Complete the email form by writing
                                your skills and interests.
                            </v-card-text>
                        </v-card>
                    </v-flex>
                </v-layout>
            </v-container>
        </section>

        <section>
            <v-container>
                <v-layout>
                    <v-flex xs12 class="text-xs-center">
                        <img alt="Welcome Logo" height="200px" :src="imageLink.logo"/>
                    </v-flex>
                </v-layout>
            </v-container>
        </section>
    </div>
</template>

<script>
    export default {
        name: "Welcome",
        data: function() {
            return {
                imageLink: {
                    sub_main: require('../assets/background.jpeg'),
                    logo: require('../assets/welcome_logo.png'),
                    social_cover: require('../assets/social.jpg')
                }
            };
        },
        beforeMount() {
            this.$store.commit('app/setTitle', 'Collaborative Markdown Editor');
            document.title = 'Collaborative Markdown Editor';
        }
    };
</script>

<style scoped>

</style>

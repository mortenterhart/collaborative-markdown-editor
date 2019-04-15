<template>
  <v-app>
    <v-navigation-drawer
            v-model="drawer"
            :clipped="clipped"
            fixed
            app
    >
      <Drawer/>
    </v-navigation-drawer>
    <v-toolbar
            :clipped-left="clipped"
            fixed
            app
    >
      <v-toolbar-side-icon @click="drawer = !drawer" />
      <v-btn
              icon
              @click.stop="clipped = !clipped"
      >
        <v-icon>web</v-icon>
      </v-btn>
      <v-toolbar-title v-text="title" />
      <v-spacer />
      <v-toolbar-items>
        <v-btn flat @click="$store.commit('login/showLoginDialog')">Login</v-btn>
      </v-toolbar-items>
    </v-toolbar>
    <v-content>
      <Login/>
      <v-container fluid fill-height>
        <v-layout row>
          <v-flex xs6 pr-2>
            <MDE @contentWasChanged="content = $event"/>
          </v-flex>
          <v-flex xs6 pl-2>
            <Preview :content="content" />
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
import MDE from './components/Editor'
import Preview from './components/Preview'
import Login from './components/Login'
import Drawer from "./components/Drawer";

export default {
  name: 'App',
  components: {
    Drawer,
    MDE,
    Preview,
    Login
  },
  data() {
    return {
      content: '',
      clipped: false,
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
  }
}
</script>

<template>
    <div>
        <v-img :aspect-ratio="16/9" src="https://cdn.vuetifyjs.com/images/parallax/material.jpg">
            <v-layout pa-2 column fill-height class="lightbox white--text">
                <v-spacer></v-spacer>
                <v-flex shrink>
                    <div class="subheading">Jonathan Lee</div>
                    <div class="body-1">heyfromjonathan@gmail.com</div>
                </v-flex>
            </v-layout>
        </v-img>

        <v-list>
            <template v-for="(item, i) in items">
                <v-divider v-if="item.divider" :key="i"></v-divider>
                <v-list-tile v-else :key="item.title">
                    <v-list-tile-action>
                        <v-icon>{{ item.icon }}</v-icon>
                    </v-list-tile-action>
                    <v-list-tile-title>{{ item.title }}</v-list-tile-title>
                </v-list-tile>
            </template>
        </v-list>
    </div>
</template>

<script>
    export default {
        name: "Drawer",
        data: () => ({
            items: [
                { icon: 'inbox', title: 'Inbox' },
                { icon: 'star', title: 'Starred' },
                { icon: 'send', title: 'Sent mail' },
                { icon: 'drafts', title: 'Drafts' },
                { divider: true },
                { icon: 'mail', title: 'All mail' },
                { icon: 'delete', title: 'Trash' },
                { icon: 'error', title: 'Spam' }
            ]
        })
    }
</script>

<style scoped>
    .lightbox {
        box-shadow: 0 0 20px inset rgba(0, 0, 0, 0.2);
        background-image: linear-gradient(to top, rgba(0, 0, 0, 0.4) 0%, transparent 72px);
    }
</style>

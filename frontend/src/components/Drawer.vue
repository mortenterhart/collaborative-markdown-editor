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

        <template v-if="showOverview">
            <v-list>
                <v-list-tile>
                    <v-list-tile-title>Your documents</v-list-tile-title>
                </v-list-tile>
                <v-divider/>
                <template v-for="(doc, i) in docs">
                    <v-list-tile v-bind:key="i">
                        <v-list-tile-action>
                            <v-icon>{{ doc.icon }}</v-icon>
                        </v-list-tile-action>
                        <v-list-tile-title>{{ doc.title }}</v-list-tile-title>
                        <v-spacer />
                        <v-icon @click="showDocumentHistory(doc)">history</v-icon>
                    </v-list-tile>
                </template>
            </v-list>
        </template>
        <template v-else>
            <v-list>
                <v-list-tile>
                    <v-list-tile-title>Document history</v-list-tile-title>
                    <v-spacer/>
                    <v-icon @click="showOverview = true">keyboard_backspace</v-icon>
                </v-list-tile>
                <v-divider/>
                <template v-for="(changes, i) in currentDocument.history">
                    <v-list-tile v-bind:key="i">
                        <v-list-tile-action>
                            {{ currentDocument.history.length - i }}
                        </v-list-tile-action>
                        <v-list-tile-title>{{ changes }}</v-list-tile-title>
                        <v-spacer />
                        <v-icon>done</v-icon>
                    </v-list-tile>
                </template>
            </v-list>
        </template>
    </div>
</template>

<script>
    export default {
        name: "Drawer",
        data: () => ({
            showOverview: true,
            currentDocument: {},
            docs: [
                { icon: 'person', title: 'Studienarbeit', history: ['10.04.2019', '06.04.2019', '05.04.2019']},
                { icon: 'group', title: 'Projektarbeit', history: ['11.04.2019', '01.04.2019']},
                { icon: 'group', title: 'Jave EE', history: ['20.04.2019', '16.04.2019', '05.04.2019']},
            ]
        }),
        methods: {
            showDocumentHistory: function(doc) {
                this.showOverview = false;
                this.currentDocument = doc;
            }
        }
    }
</script>

<style scoped>
    .lightbox {
        box-shadow: 0 0 20px inset rgba(0, 0, 0, 0.2);
        background-image: linear-gradient(to top, rgba(0, 0, 0, 0.4) 0%, transparent 72px);
    }
</style>

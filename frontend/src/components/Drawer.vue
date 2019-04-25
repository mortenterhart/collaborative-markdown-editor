<template>
    <div>
        <v-img :aspect-ratio="16/9" :src="require('../assets/work.jpeg')">
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
                    <v-list-tile-title>Your Documents</v-list-tile-title>
                </v-list-tile>
                <v-divider/>
                <template v-for="(doc, i) in docs">
                    <v-list-tile v-bind:key="i">
                        <v-list-tile-action>
                            <v-icon>{{ doc.icon }}</v-icon>
                        </v-list-tile-action>
                        <v-list-tile-title>{{ doc.title }}</v-list-tile-title>
                        <v-spacer />
                        <v-icon class="mr-2" @click="showCollaboratorList(doc)">list</v-icon>
                        <v-icon @click="showDocumentHistory(doc)">history</v-icon>
                    </v-list-tile>
                </template>
                <v-list-tile>
                    <v-text-field
                            v-model.trim="documentName"
                            label="New Document"
                            single-line
                    ></v-text-field>
                    <v-icon @click="addDocument">note_add</v-icon>
                </v-list-tile>
            </v-list>
        </template>
        <template v-if="showHistory">
            <v-list>
                <v-list-tile>
                    <v-list-tile-title>Document History</v-list-tile-title>
                    <v-spacer/>
                    <v-icon @click="showOverview = true; showHistory =  false">keyboard_backspace</v-icon>
                </v-list-tile>
                <v-divider/>
                <template v-for="(changes, i) in currentDocument.history">
                    <v-list-tile v-bind:key="i">
                        <v-list-tile-action>
                            {{ currentDocument.history.length - i }}
                        </v-list-tile-action>
                        <v-list-tile-title>{{ changes }}</v-list-tile-title>
                        <v-spacer />
                        <v-icon @click="revertHistory">done</v-icon>
                    </v-list-tile>
                </template>
            </v-list>
        </template>
        <template v-if="showCollaborators">
            <v-list>
                <v-list-tile>
                    <v-list-tile-title>The Collaborators</v-list-tile-title>
                    <v-spacer/>
                    <v-icon @click="showOverview = true; showCollaborators = false">keyboard_backspace</v-icon>
                </v-list-tile>
                <v-divider/>
                <template v-for="(collaborator, i) in currentDocument.collaborators">
                    <v-list-tile v-bind:key="i">
                        <v-list-tile-action>
                            <v-icon>person</v-icon>
                        </v-list-tile-action>
                        <v-list-tile-title>{{ collaborator }}</v-list-tile-title>
                        <v-spacer />
                        <v-icon @click="removeCollaborator">cancel</v-icon>
                    </v-list-tile>
                </template>
                <v-list-tile>
                    <v-text-field
                            v-model.trim="collaboratorName"
                            label="Add Collaborators"
                            single-line
                    ></v-text-field>
                    <v-icon @click="addCollaborator">person_add</v-icon>
                </v-list-tile>
            </v-list>
        </template>
    </div>
</template>

<script>
    import Cookies from 'js-cookie';

    export default {
        name: "Drawer",
        data: () => ({
            showOverview: true,
            showHistory: false,
            showCollaborators: false,
            documentName: '',
            collaboratorName: '',
            currentDocument: {},
            docs: [
                { icon: 'person', title: 'Studienarbeit', history: ['10.04.2019', '06.04.2019', '05.04.2019'], collaborators: ['Morten Terhart', 'Micha Spahr']},
                { icon: 'group', title: 'Projektarbeit', history: ['11.04.2019', '01.04.2019'], collaborators: ['Phillip Seitz', 'Jacob Krauth']},
                { icon: 'group', title: 'Jave EE', history: ['20.04.2019', '16.04.2019', '05.04.2019'], collaborators: ['Fabian Schulz']},
            ]
        }),
        methods: {
            showDocumentHistory: function(doc) {
                this.showOverview = false;
                this.showHistory = true;
                this.currentDocument = doc;
            },
            showCollaboratorList: function(doc) {
                this.showOverview = false;
                this.showCollaborators = true;
                this.currentDocument = doc;
            },
            addDocument: function() {
                if (this.documentName === '') {
                    this.$snotify.error(
                        'Document needs a name',
                        'Error'
                    );
                    return;
                }

                this.axios.post('/document/add',
                    {
                        documentName: this.documentName
                    },
                    {
                        headers: {
                            'Cookie': Cookies.get("JSESSIONID"),
                            'Content-Type': 'application/json'
                        }
                    }).then(() => {
                        this.documentName = '';
                        this.$snotify.success(
                            'Document was created',
                            'Success'
                        );
                    }).catch((error) => {
                            this.$snotify.error(
                                error.response.data.message,
                                'Error'
                            );
                        }
                    );
            },
            removeCollaborator: function() {
                this.$snotify.success(
                    'Collaborator was removed',
                    'Success'
                );
            },
            addCollaborator: function() {
                if (this.collaboratorName === '') {
                    this.$snotify.error(
                        'Enter a collaborator name',
                        'Error'
                    );
                    return;
                }

                this.$snotify.success(
                    'Collaborator was added',
                    'Success'
                );
            },
            revertHistory: function() {
                this.$snotify.success(
                    'History was reverted',
                    'Success'
                );
            },

        }
    }
</script>

<style scoped>
    .lightbox {
        box-shadow: 0 0 50px inset rgba(0, 0, 0, 1);
        background-image: linear-gradient(to top, rgba(0, 0, 0, 0.4) 0%, transparent 72px);
    }
</style>

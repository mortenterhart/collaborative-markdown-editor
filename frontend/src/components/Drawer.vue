<template>
    <div>
        <v-img :aspect-ratio="16/9" :src="require('../assets/work.jpeg')">
            <v-layout pa-2 column fill-height class="lightbox white--text">
                <v-spacer></v-spacer>
                <v-flex shrink>
                    <div class="subheading">{{ $store.state.login.user.name }}</div>
                    <div class="body-1">{{ $store.state.login.user.mail }}</div>
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
                    <v-hover>
                        <v-list-tile v-bind:key="i" slot-scope="{ hover }" :class="`elevation-${hover ? 8 : 0}`" :style="`cursor: ${hover ? 'pointer' : 'default'}`">
                            <v-list-tile-action>
                                <v-icon>{{ doc.icon }}</v-icon>
                            </v-list-tile-action>
                            <v-list-tile-title @click="openDocument(doc.document)">{{ doc.document.name }}</v-list-tile-title>
                            <v-spacer />
                            <v-icon class="mr-2" @click="showCollaboratorList(doc)">list</v-icon>
                            <!--<v-icon @click="showDocumentHistory(doc.document)">history</v-icon>-->
                            <v-icon @click="removeDocument(doc)">cancel</v-icon>
                        </v-list-tile>
                    </v-hover>
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
                    <v-hover>
                        <v-list-tile v-bind:key="i" slot-scope="{ hover }" :class="`elevation-${hover ? 8 : 0}`" :style="`cursor: ${hover ? 'pointer' : 'default'}`">
                            <v-list-tile-action>
                                <v-icon>person</v-icon>
                            </v-list-tile-action>
                            <v-list-tile-title>{{ collaborator.user.name }}</v-list-tile-title>
                            <v-spacer />
                            <v-icon @click="removeCollaborator(index, collaborator.id)">cancel</v-icon>
                        </v-list-tile>
                    </v-hover>
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
            <template v-if="$store.state.login.user.id === currentDocument.document.repo.owner.id">
                <br/>
                <v-list>
                    <v-list-tile>
                        <v-list-tile-title>Transfer Ownership</v-list-tile-title>
                    </v-list-tile>
                    <v-divider/>
                    <v-list-tile>
                        <v-text-field
                                v-model.trim="transferOwnershipName"
                                label="Name of collaborator"
                                single-line
                        ></v-text-field>
                        <v-icon @click="transferOwnership">send</v-icon>
                    </v-list-tile>
                </v-list>
            </template>
        </template>
    </div>
</template>

<script>
    export default {
        name: "Drawer",
        data: () => ({
            showOverview: true,
            showHistory: false,
            showCollaborators: false,
            documentName: '',
            collaboratorName: '',
            transferOwnershipName: '',
            currentDocument: { document: { repo: { owner: { id: -1 } } } },
            docs: [
                { icon: '', document: { name: '', id: 0 }, history: [''], collaborators: ['']},
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
                            'Content-Type': 'application/json'
                        },
                        withCredentials: true
                    }).then(() => {
                        this.documentName = '';
                        this.$snotify.success(
                            'Document was created',
                            'Success'
                        );
                        this.fetchDocuments(false)
                    }).catch((error) => {
                        this.$snotify.error(
                            error.response.data.message,
                            'Error'
                        );
                    }
                );
            },
            removeDocument(doc) {
                this.docs.splice(this.docs.findIndex(x => x.document.id === doc.document.id), 1)
                this.$snotify.success(
                    'Document was removed',
                    'Success'
                );
            },
            removeCollaborator: function(index, collaboratorId) {
                this.axios.post('/collaborators/remove',
                    {
                        documentId: this.currentDocument.document.id,
                        collaboratorId: collaboratorId
                    },
                    {
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        withCredentials: true
                    }).then(() => {
                        this.currentDocument.collaborators.splice(index, 1)
                        if (this.currentDocument.collaborators.length === 0) {
                            this.currentDocument.icon = "person"
                        }
                        this.$snotify.success(
                            'Collaborator was removed',
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
            addCollaborator: function() {
                if (this.collaboratorName === '') {
                    this.$snotify.error(
                        'Enter a collaborator name',
                        'Error'
                    );
                    return;
                }

                this.axios.post('/collaborators/add',
                    {
                        collaboratorUsername: this.collaboratorName,
                        documentId: this.currentDocument.document.id
                    },
                    {
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        withCredentials: true
                    }).then(() => {
                        this.collaboratorName = '';
                        this.$snotify.success(
                            'Collaborator was added',
                            'Success'
                        );
                        this.fetchDocuments(true)
                    }).catch((error) => {
                        this.$snotify.error(
                            error.response.data.message,
                            'Error'
                        );
                    }
                );
            },
            transferOwnership: function() {
                if (this.transferOwnershipName === '') {
                    this.$snotify.error(
                        'Enter a collaborator name',
                        'Error'
                    );
                    return;
                }

                this.transferOwnershipName = ''
                this.currentDocument.document.repo.owner.id = -1
                this.$snotify.success(
                    'Ownership was transferred',
                    'Success'
                );
            },
            revertHistory: function() {
                this.$snotify.success(
                    'History was reverted',
                    'Success'
                );
            },
            fetchDocuments: function(setCurrentDocument) {
                this.axios.get('/document/all',
                    {
                        withCredentials: true
                    }).then((response) => {
                        this.docs = response.data;
                        if (setCurrentDocument)
                            this.currentDocument = this.docs.find(x => x.document.id === this.currentDocument.document.id)
                    }).catch((error) => {
                        this.$snotify.error(
                            error.response.data.message,
                            'Error'
                        );
                    }
                );
            },
            openDocument: function(doc) {
                this.$router.push(`/doc/${doc.id}`)
                this.$store.commit('app/incEditorKey')
            }
        },
        beforeMount() {
            this.fetchDocuments(false);
        }
    }
</script>

<style scoped>
    .lightbox {
        box-shadow: 0 0 50px inset rgba(0, 0, 0, 1);
        background-image: linear-gradient(to top, rgba(0, 0, 0, 0.4) 0%, transparent 72px);
    }
</style>

<template>
    <v-container fluid fill-height>
        <v-layout row>
            <beautiful-chat
                    :style="`z-index: 99`"
                    :participants="this.$store.state.app.otherCollaborators"
                    :onMessageWasSent="onMessageWasSent"
                    :messageList="messageList"
                    :newMessagesCount="newMessagesCount"
                    :isOpen="isChatOpen"
                    :close="closeChat"
                    :open="openChat"
                    :showEmoji="false"
                    :showFile="false"
                    :showTypingIndicator="showTypingIndicator"
                    :colors="colors"
                    :alwaysScrollToBottom="alwaysScrollToBottom"
                    :messageStyling="messageStyling"></beautiful-chat>
            <v-flex d-flex xs12 md6 pr-2>
                <SimpleMDE :key="this.$store.state.app.editorKey" @contentWasChanged="content = $event"
                     @sendWebSocketMessage="sendWebSocketMessage($event)" ref="editor"></SimpleMDE>
            </v-flex>
            <v-flex d-flex xs12 md6 pl-2>
                <Preview :content="content"/>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import SimpleMDE from './Editor';
    import Preview from './Preview';
    import axios from 'axios';

    export default {
        name: "Document",
        data() {
            return {
                socket: null,
                content: '',
                messageList: [],
                newMessagesCount: 0,
                isChatOpen: false,
                showTypingIndicator: '',
                colors: {
                    header: {
                        bg: '#4e8cff',
                        text: '#ffffff'
                    },
                    launcher: {
                        bg: '#4e8cff'
                    },
                    messageList: {
                        bg: '#ffffff'
                    },
                    sentMessage: {
                        bg: '#4e8cff',
                        text: '#ffffff'
                    },
                    receivedMessage: {
                        bg: '#eaeaea',
                        text: '#222222'
                    },
                    userInput: {
                        bg: '#f4f7f9',
                        text: '#565867'
                    }
                },
                alwaysScrollToBottom: false,
                messageStyling: true
            }
        },
        components: {
            SimpleMDE,
            Preview
        },
        watch: {
            '$route'() {
                this.initWebSocketConnection();
            }
        },
        beforeRouteEnter(to, from, next) {
            axios.post(location.origin + location.pathname.replace(/\/?$/, "") + '/api/document/hasAccess',
                {
                    documentId: Number(to.params.id)
                },
                {
                    withCredentials: true
                }
            ).then(() => {
                next();
            }).catch(() => {
                next('/Forbidden');
            });
        },
        beforeRouteUpdate(to, from, next) {
            axios.post(location.origin + location.pathname.replace(/\/?$/, "") + '/api/document/hasAccess',
                {
                    documentId: Number(to.params.id)
                },
                {
                    withCredentials: true
                }
            ).then(() => {
                next();
            }).catch(() => {
                next('/Forbidden');
            });
        },
        mounted() {
            this.initWebSocketConnection();

            let vm = this;
            window.addEventListener('unload', function() {
                if (vm.socket) {
                    vm.socket.close();
                }
            });
        },
        methods: {
            initWebSocketConnection() {
                this.messageList = [];
                if (this.socket) {
                    this.socket.close();
                }
                this.socket = new WebSocket(this.getWebSocketURL());

                let vm = this;
                this.socket.onmessage = function(event) {
                    const eventData = JSON.parse(event.data.toString());
                    vm.$refs.editor.handleEditorWebSocketEvents(eventData);

                    switch (eventData.messageType) {
                        case "ChatMessage": {
                            vm.onMessageWasSent({
                                author: String(eventData.userId),
                                type: 'text',
                                data: { text: eventData.msg }
                            });
                            break;
                        }
                        case "UserJoined": {
                            vm.onMessageWasSent({
                                type: 'system',
                                data: { text: JSON.parse(eventData.msg).name + ' joined the chat.' }
                            });
                            break;
                        }
                        case "UserLeft": {
                            vm.onMessageWasSent({
                                type: 'system',
                                data: { text: JSON.parse(eventData.msg).name + ' left the chat.' }
                            });
                            break;
                        }
                    }
                };

                this.onMessageWasSent({
                    type: 'system',
                    data: { text: 'Welcome to the chat! Try the !joke command if you\'re bored :)' }
                });
            },
            sendWebSocketMessage(msg) {
                this.socket.send(msg);
            },
            onMessageWasSent(message) {
                if (this.handleUserCommand(message)) {
                    return;
                }

                this.messageList.push(message);

                if (message.author === 'me') {
                    const msg = JSON.stringify({
                        "userId": this.$store.state.login.user.id,
                        "docId": Number(this.$route.params.id),
                        "cursorPos": -1,
                        "docState": -1,
                        "msg": message.data.text,
                        "messageType": "ChatMessage"
                    });
                    this.socket.send(msg);
                }
            },
            handleUserCommand(message) {
                if (!message.data.text || message.type === 'system' || !message.data.text.trim().startsWith('!')) {
                    return false;
                }

                const command = message.data.text.trim().substring(1).toLowerCase();
                switch (command) {
                    case 'joke': {
                        this.axios.get('https://sv443.net/jokeapi/category/Programming',
                            {},
                            {}).then((response) => {
                            if (response.data.type === "single") {
                                this.onMessageWasSent({ type: 'system', data: { text: response.data.joke } });
                            } else if (response.data.type === "twopart") {
                                this.onMessageWasSent({ type: 'system', data: { text: response.data.setup } });
                                let vm = this;
                                setTimeout(() => {
                                    vm.onMessageWasSent({ type: 'system', data: { text: response.data.delivery } });
                                }, 2000)
                            }
                        }).catch(() => {
                                this.onMessageWasSent({ type: 'system', data: { text: 'Error retrieving a joke' } });
                            }
                        );
                        break;
                    }
                    default: {
                        this.onMessageWasSent({ type: 'system', data: { text: `Command not found: !${command}` } });
                        break;
                    }
                }

                return true;
            },
            openChat() {
                this.isChatOpen = true;
                this.newMessagesCount = 0;
            },
            closeChat() {
                this.isChatOpen = false;
            },
            getWebSocketURL() {
                const wsProtocol = location.protocol.startsWith('https') ? 'wss' : 'ws';
                const pathname = location.pathname.replace(/\/?$/, '');
                const user = this.$store.state.login.user;
                const wsPath = `${this.$route.params.id}/${user.name}/${user.id}`;
                return `${wsProtocol}://${location.host}${pathname}/ws/${wsPath}`;
            }
        }
    };
</script>

<style scoped>

</style>

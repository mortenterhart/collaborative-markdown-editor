<template>
    <v-container fluid fill-height>
        <beautiful-chat
                :participants="this.$store.state.app.otherCollaborators"
                :onMessageWasSent="onMessageWasSent"
                :messageList="messageList"
                :newMessagesCount="newMessagesCount"
                :isOpen="isChatOpen"
                :close="closeChat"
                :open="openChat"
                :showEmoji="true"
                :showFile="false"
                :showTypingIndicator="showTypingIndicator"
                :colors="colors"
                :alwaysScrollToBottom="alwaysScrollToBottom"
                :messageStyling="messageStyling"
                @onType="handleOnType" />
        <v-layout row>
            <v-flex xs6 pr-2>
                <MDE :key="this.$store.state.app.editorKey" @contentWasChanged="content = $event" @sendWebSocketMessage="sendWebSocketMessage($event)" ref="editor"/>
            </v-flex>
            <v-flex xs6 pl-2>
                <Preview :content="content"/>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import MDE from './Editor'
    import Preview from './Preview'

    export default {
        name: "Document",
        data() {
            return {
                socket: null,
                content: '',
                messageList: [], // the list of the messages to show, can be paginated and adjusted dynamically
                newMessagesCount: 0,
                isChatOpen: false, // to determine whether the chat window should be open or closed
                showTypingIndicator: '', // when set to a value matching the participant.id it shows the typing indicator for the specific user
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
                }, // specifies the color scheme for the component
                alwaysScrollToBottom: false, // when set to true always scrolls the chat to the bottom when new events are in (new message, user starts typing...)
                messageStyling: true // enables *bold* /emph/ _underline_ and such (more info at github.com/mattezza/msgdown)
            }
        },
        components: {
            MDE,
            Preview
        },
        watch: {
            '$route' () {
                this.initWebSocketConnection()
            }
        },
        mounted() {
            // TODO: check if user is logged in and if user has access to the doc
            this.initWebSocketConnection()
        },
        methods: {
            initWebSocketConnection() {
                if (this.socket) this.socket.close();
                this.socket = new WebSocket(this.getWebSocketURL());

                let vm = this;
                this.socket.onmessage = function (event) {
                    const eventData = JSON.parse(event.data.toString())
                    vm.$refs.editor.handleEditorWebSocketEvents(eventData);
                    console.log(eventData)
                    switch (eventData.messageType) {
                        case "ChatMessage":
                            vm.onMessageWasSent({ author: String(eventData.userId), type: 'text', data: { text: eventData.msg } })
                            break
                        case "UserJoined":
                            vm.onMessageWasSent({ type: 'system', data: { text: JSON.parse(eventData.msg).name + ' joined the chat.' } })
                            break;
                        case "UserLeft":
                            vm.onMessageWasSent({ type: 'system', data: { text: JSON.parse(eventData.msg).name + ' left the chat.' } })
                            break;
                    }
                };
            },
            sendWebSocketMessage(msg) {
                this.socket.send(msg)
            },
            sendMessage(text) {
                if (text.length > 0) {
                    this.newMessagesCount = this.isChatOpen ? this.newMessagesCount : this.newMessagesCount + 1
                    this.onMessageWasSent({ author: 'support', type: 'text', data: { text } })
                }
            },
            onMessageWasSent(message) {
                // called when the user sends a message
                this.messageList = [...this.messageList, message]

                if (message.author === 'me') {
                    const msg = JSON.stringify({
                        "userId": this.$store.state.login.user.id,
                        "docId": Number(this.$route.params.id),
                        "cursorPos": -1,
                        "msg": message.data.text,
                        "messageType": "ChatMessage"
                    })
                    this.socket.send(msg);
                }
            },
            openChat() {
                // called when the user clicks on the fab button to open the chat
                this.isChatOpen = true
                this.newMessagesCount = 0
            },
            closeChat() {
                // called when the user clicks on the botton to close the chat
                this.isChatOpen = false
            },
            handleScrollToTop() {
                // called when the user scrolls message list to top
                // leverage pagination for loading another page of messages
            },
            handleOnType() {
                // Emit typing event
            },
            getWebSocketURL() {
                return `ws://${location.hostname}:${location.port}/CMD/ws/${this.$route.params.id}/${this.$store.state.login.user.name}/${this.$store.state.login.user.id}`
            }
        }
    }
</script>

<style scoped>

</style>
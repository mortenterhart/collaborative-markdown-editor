import Cookie from "js-cookie";

const state = {
    username: '',
    showLoginDialog: false,
    isLoggedIn: !!Cookie.get("JSESSIONID"),
};

const getters = {

};

const actions = {

};

const mutations = {
    showLoginDialog (state) {
        state.showLoginDialog = true
    },
    hideLoginDialog (state) {
        state.showLoginDialog = false
    },
    setIsLoggedIn: (state, newValue) => {
        state.isLoggedIn = newValue
    },
    setUsername: (state, newValue) => {
        state.username = newValue
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}

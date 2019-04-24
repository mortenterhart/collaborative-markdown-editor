import Cookie from "js-cookie";

const state = {
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
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}

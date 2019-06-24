const state = {
    user: { id: 0, name: "", mail: "" },
    showLoginDialog: false,
    isLoggedIn: false,
};

const getters = {};

const actions = {};

const mutations = {
    showLoginDialog(state) {
        state.showLoginDialog = true;
    },
    hideLoginDialog(state) {
        state.showLoginDialog = false;
    },
    setIsLoggedIn: (state, newValue) => {
        state.isLoggedIn = newValue;
    },
    setUser: (state, newValue) => {
        state.user = newValue;
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
};

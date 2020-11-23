const app = new Vue({
    store,
    el: '#app',
    data: {
        websocket: null,
    },
    computed: {
        showModal: function () {
            return this.$store.state.showModal;
        },
        gameRunning: function () {
            return this.$store.state.gameRunning;
        },
        settingsModalConfig: function () {
            return {
                closeable: this.$store.state.gameRunning,
                title: this.$store.state.gameRunning
                    ? 'Reconfigure'
                    : 'Create Game',
                buttonText: 'Restart Game',
            };
        },
        blurStyle: function () {
            if (this.$store.state.showModal && this.$store.state.gameRunning)
                return {filter: 'blur(1.0rem)'};
            else return {};
        },
    }
});

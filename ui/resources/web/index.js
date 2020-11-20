const app = new Vue({
    el: '#app',
    computed: {
        settingsConfig: function () {
            return {
                closeable: this.gameRunning,
                title: this.gameRunning ? 'Reconfigure' : 'Create Game',
                buttonText: 'Restart Game',
            };
        },
        blurStyle: function () {
            if (this.settingsVisible && this.gameRunning)
                return { filter: 'blur(1.0rem)' };
            else return {};
        },
    },
    methods: {
        submitSettings: function (e) {
            e.preventDefault();
            this.sendToSocket('reconfigure', { ...this.settings });
        },
        reconfigure: function () {
            this.settingsVisible = true;
        },
        restart: function () {},
        showGameOver: function (points) {},
        settingsClose: function () {
            this.settingsVisible = false;
        },
    },
    created: function () {
        this.initialize();
    },
});

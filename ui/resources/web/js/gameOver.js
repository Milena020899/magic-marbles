Vue.component('game-over', {
    store,
    template:
        `<div id="game-over-wrapper">
            <div id="game-over-message">You achieved {{points}} Points</div>
            <button class="button" @click="restart">Restart</button>
        </div>`,
    computed: {
        points: function () {
            return this.$store.state.points
        }
    },
    methods: {
        restart: function () {
            this.$store.dispatch('restart');
        }
    }
})
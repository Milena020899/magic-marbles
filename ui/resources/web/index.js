const app = new Vue({
    el: '#app',
    data: {
        websocket: null,
        gameRunning: true,
        storedSettings: {
            width: 0,
            height: 0,
            connectedMarbles: 0,
            remainingMarbleDeduction: 1,
        },
        settingsVisible: false,
        field: [
            [null, { color: 'red' }, { color: 'red' }],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
            [
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
                { color: 'red' },
            ],
        ],
        points: 0,
    },
    computed: {
        settingsConfig: function () {
            return {
                closeable: this.gameRunning,
                title: this.gameRunning ? 'Reconfigure' : 'Create Game',
                buttonText: 'Restart Game',
            };
        },
    },
    methods: {
        marbleStyle: function (marble) {
            return marble
                ? {
                      cursor: 'pointer',
                      background: marble.color,
                      border: `4px solid ${
                          marble.highlight ? '' : 'transparent'
                      }`,
                  }
                : {
                      background: 'transparent',
                      border: '4px solid transparent',
                  };
        },
        submitSettings: function (e) {
            this.websocket.send(
                JSON.stringify({
                    type: 'reconfigure',
                    payload: { ...this.settings },
                })
            );
            e.preventDefault();
        },

        onReceive: function ({ type, payload }) {
            switch (type) {
                case 'newGame':
                    this.gameRunning = true;
                    this.settingsVisible = false;
                case 'state':
                    this.updateState(payload);
                    break;
                case 'hover':
                    this.updateHover(payload.marbles);
                    break;
                case 'settings':
                    this.updateSettings(payload);
                    break;
                case 'gameOver':
                    this.showGameOver(payload);
                    break;
            }
        },
        updateState: function (gameState) {
            this.field = gameState.field.map((col) =>
                col.map((marble) =>
                    marble ? { ...marble, highlight: false } : null
                )
            );
            this.points = gameState.points;
            this.gameRunning = true;
        },
        updateSettings: function (settings) {
            this.settings.width = settings.width;
            this.settings.height = settings.height;
            this.settings.connectedMarbles = settings.connectedMarbles;
            this.settings.remainingMarbleDeduction =
                settings.remainingMarbleDeduction;
        },
        updateHover: function (marbles) {
            marbles.forEach((coord) => {
                this.field[coord.column][coord.row].highlight = true;
            });
        },
        showGameOver: function (points) {},
        onHover: function (column, row) {
            this.debounce('startHoverDebounce', () =>
                this.sendToSocket('hover', { column, row })
            );
        },
        endHover: function () {
            this.debounce('endHoverDebounce', () => {
                this.field.forEach((column) =>
                    column.forEach((marble) => {
                        if (marble) {
                            marble.highlight = false;
                        }
                    })
                );
            });
        },
        marbleSelected: function (column, row) {
            this.sendToSocket('move', { column, row });
        },
        changeSettingsMidGame: function () {
            this.settingsVisible = true;
        },
        settingsClose: function () {
            this.settingsVisible = false;
        },
        debounce: function (debounceFuncId, func, timeout = 250) {
            if (this[debounceFuncId]) {
                clearTimeout(this[debounceFuncId]);
            }
            this[debounceFuncId] = setTimeout(func, timeout);
        },
        sendToSocket: function (type, payload) {
            this.websocket.send(JSON.stringify({ type, payload }));
        },
    },
    created: function () {
        let socket = new WebSocket(`ws://${window.location.host}/ws`);
        socket.onerror = function () {
            console.log('ERROR');
        };

        socket.onclose = (e) => {
            console.log('CLOSED');
        };

        socket.onmessage = (e) => {
            this.onReceive(JSON.parse(e.data));
        };

        this.websocket = socket;
    },
});

Vue.mixin({
    data: function () {
        return {
            websocket: null,
            field: [],
            points: 0,
            gameRunning: true,
            settingsVisible: false,
            settings: {
                width: 0,
                height: 0,
                connectedMarbles: 0,
                remainingMarbleDeduction: 1,
            },
        };
    },
    methods: {
        initialize: function () {
            let socket = new WebSocket(`ws://${window.location.host}/ws`);
            socket.onerror = function () {
                console.log('ERROR');
            };

            socket.onclose = (e) => {
                console.log('CLOSED');
            };

            socket.onmessage = (e) => {
                try {
                    this.onReceive(JSON.parse(e.data));
                } catch (error) {}
            };

            this.websocket = socket;
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
        move: function (column, row) {
            this.sendToSocket('move', { column, row });
        },
        reconfigure: function () {
            this.settingsVisible = true;
        },
    },
});

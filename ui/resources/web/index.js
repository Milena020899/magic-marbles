const app = new Vue({
    el: "#app",
    data: {
        websocket: null,
        gameRunning: false,
        field: [],
        points: 0,
        settings: {
            width: 0,
            height: 0,
            connectedMarbles: 0,
            remainingMarbleDeduction: 1,
        },
    },
    methods: {
        marbleStyle: function (marble) {
            console.log("STYLE CALL")
            if (!marble) {
                return {background: 'transparent', border: '4px solid transparent'}
            } else {
                return {
                    cursor: 'pointer',
                    background: marble.color,
                    border: `4px solid ${marble.highlight ? '' : 'transparent'}`
                }
            }
        },
        settingsSubmit: function (e) {
            let req = {
                type: "reconfigure", payload: {
                    ...this.settings
                }
            }
            this.websocket.send(JSON.stringify(req))
            e.preventDefault()
        },

        onReceive: function ({type, payload}) {
            switch (type) {
                case "state":
                    this.updateState(payload)
                    break;
                case "hover":
                    this.updateHover(payload.marbles)
                    break;
                case "settings":
                    this.updateSettings(payload)
                    break;
                case "gameOver":
                    this.showGameOver(payload)
                    break;
            }
        },
        updateState: function (gameState) {
            this.field = gameState.field
                .map(col => col
                    .map(marble => marble ? {...marble, highlight: false} : null))
            this.points = gameState.points
            this.gameRunning = true
        },
        updateSettings: function (settings) {
            this.settings.width = settings.width
            this.settings.height = settings.height
            this.settings.connectedMarbles = settings.connectedMarbles
            this.settings.remainingMarbleDeduction = settings.remainingMarbleDeduction
        },
        updateHover: function (marbles) {
            marbles.forEach(coord => {
                this.field[coord.column][coord.row].highlight = true
            })
        },
        showGameOver: function (points) {

        },
        onHover: function (column, row) {
            if (this.onHoverTimeout)
                clearTimeout(this.onHoverTimeout)

            this.onHoverTimeout = setTimeout(() => {
                this.sendToSocket("hover", {column, row})
            }, 500);

        },
        endHover: function () {
            if (this.onHoverOverTimeout) {
                clearTimeout(this.onHoverOverTimeout)
            }

            this.onHoverOverTimeout = setTimeout(() => {
                    this.field.forEach(column => column.forEach(marble => {
                        if (marble) {
                            marble.highlight = false
                        }
                    }))
                }, 500
            )
        },
        marbleSelected: function (column, row) {
            this.sendToSocket("move", {column, row})
        },
        sendToSocket: function (type, payload) {
            this.websocket.send(JSON.stringify({type, payload}))
        }
    },
    created: function () {
        let socket = new WebSocket(`ws://${window.location.host}/ws`)
        socket.onerror = function () {
            console.log("ERROR")
        }

        socket.onclose = (e) => {
            console.log("CLOSED")
        }

        socket.onmessage = (e) => {
            this.onReceive(JSON.parse(e.data))
        }

        this.websocket = socket
    }
})
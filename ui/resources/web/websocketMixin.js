var websocketMixin = {
    data: function () {
        return { websocket: null };
    },
    methods: {
        sendToSocket: function (type, payload) {
            this.websocket.send(JSON.stringify({ type, payload }));
        },
        onMessage: function (type, func) {
            this.websocket?.addEventListener('message', ({ data }) => {
                try {
                    let parsedData = JSON.parse(data);
                    if (parsedData.type == type) {
                        func(data.payload);
                    }
                } catch (e) {}
            });
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

        this.websocket = socket;
    },
};

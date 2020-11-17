class Game {
    websocket = null
    firstGameStarted = false

    initialize() {
        let socket = new WebSocket(`ws://${window.location.host}/ws`)
        socket.onerror = () => {
            console.log("ERROR")
        }

        socket.onclose = (e) => {
            console.log("CLOSED")
        }

        socket.onmessage = (e) => {
            console.log(e.data)
        }

        this.websocket = socket
        this.registerClickHandler()
    }

    registerClickHandler() {
        $("#settings").submit((event) => {
            console.log("Y")
            event.preventDefault()
            let formValues = $("#settings").serializeArray()
            let formObj = {}
            formValues.forEach((object) => {
                formObj[object.name] = object.value
            })
            this.reconfigure(formObj)
        })
    }

    reconfigure(settings) {
        console.log("configure")
        let req = {type: "reconfigure", payload: settings}
        this.websocket.send(JSON.stringify(req))
    }
}

function init() {
    new Game().initialize()
}
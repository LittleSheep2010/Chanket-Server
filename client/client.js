const axios = require("axios")
const websocket = require("ws")

const input = require('readline').createInterface({input: process.stdin, output: process.stdout})

function strap2date(time){
    let datetime = new Date();
    datetime.setTime(time);
    let year = datetime.getFullYear();
    let month = datetime.getMonth() + 1;
    let date = datetime.getDate();
    let hour = datetime.getHours();
    let minute = datetime.getMinutes();
    let second = datetime.getSeconds();
    let mseconds = datetime.getMilliseconds();
    return year + "-" + month + "-" + date+" "+hour+":"+minute+":"+second+"."+mseconds;
}

function connect(url, username, password, register, logger) {
    return new Promise(resolve => {
        axios.get(`http://${url}/account/auth?username=${username}&password=${password}&auto=${register}`).then(res => {
            if(res.data.status === "Successes") {
                logger.info("[AUTHORIZATION] Authorization completed!")
                resolve(new websocket(`ws://${url}/chanket/${res.data.result}`))
            }

            else {
                logger.error("[AUTHORIZATION] Authorization failed: " + res.data["reason"])
                process.exit(1)
                resolve(null)
            }

        }).catch(err => {
            logger.err("[AUTHORIZATION] Failed to connect server")
            process.exit(1)
            resolve(null)

        })
    })
}

// AUTO REGISTER CONFIGURE
const auto_register = true;

// RECEIVE CONFIGURE
const receive = "${GLOBAL}"

let conn
input.question("[SETUP] Please enter server address: ", url => {

    // Create connection
    input.question("[SETUP] Please enter username: ", username => {
        input.question("[SETUP] Please enter password: ", password => {
            connect(url, username, password, auto_register, console).then(connection => {
                conn = connection
                conn.onopen = () => {
                    console.info("==========================================================================")
                    console.info("[SERVER] Connected!")
                    console.info("[SERVER] If you want send message, please enter it!")
                    console.info("[SERVER] If you want execute command please add `.command ` before message!")
                    console.info("                           Enjoy it!")
                    console.info("==========================================================================")
                }

                conn.onmessage = message => {
                    let data = JSON.parse(message.data)

                    if(data["type"] === "system") {
                        console.log(`[RECEIVE] (SYSTEM) ${JSON.parse(message.data)["display"]}: ${JSON.parse(message.data)["message"]}`)
                    }

                    else if(data["type"] === "chat-message") {
                        console.log(`[RECEIVE] (CHAT:${JSON.parse(message.data)["title"]}) (${strap2date(JSON.parse(message.data)["time"] * 1000)}) [${JSON.parse(message.data)["sender"]["username"]}] ${JSON.parse(message.data)["message"]}`)
                    }

                    else {
                        console.log("[RECEIVE] No-process message: ", message.data)
                    }
                }

                input.on("line", input => {
                    if(input.startsWith(".command ")) {
                        let command = input.substr(0, input.length - 9)

                        conn.send(command)
                    }

                    else {

                        conn.send(`message ${receive} "${input}"`)
                    }
                })
            })
        })
    })
})

process.on("exit", () => {
    if(conn != null) conn.close()
})
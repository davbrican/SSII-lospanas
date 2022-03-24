const WebSocket = require('ws');
const crypto = require('crypto');
const readline = require('readline');
const fs = require('fs');

var nonces = [];
const hashType = ['sha256', 'sha512', 'sha384'];
var secret;


// Function to create Nonce
function createNonce() {
    let nonce = crypto.randomBytes(128).toString('hex');
    if (nonces.includes(nonce)) {
        return createNonce();
    } else {
        return nonce;
    }
}

// Function to create HMAC
function createHmac(message, nonce, hashInput) {
    const hmac = crypto.createHmac(hashType[hashInput], secret);

    hmac.update(message + nonce);

    const hex = hmac.digest('hex');
    return hex;
}

// Function to create a report
function createReport(reportFile, content) {
    reportFile = "./Reports/" + reportFile;
    fs.appendFile(reportFile, "\n"+content, (err) => {
        if (err) {
            //console.log(err);
        } else {
            //console.log("Report created");
        }
    });
}

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.question('Escriba la clave simétrica\n', function (secretKey) {
    secret = secretKey;
    rl.close();
});

rl.on('close', function () {
    console.log("Esperando conexión...");


    const wss = new WebSocket.Server({ port: 8081 });

    // Wire up some logic for the connection event (when a client connects) 
    wss.on('connection', function connection(ws) {

        // Wire up logic for the message event (when a client sends something)
        ws.on('message', function incoming(message) {
            objectReceived = JSON.parse(message);
            if (nonces.includes(objectReceived.nonce)) {
                createReport("serverLog.txt", "El mensaje recibido es una respuesta a un ataque MiTM\n"+message+"\n");
                console.log("La operación ha sido realizada ya previamente (nonce repetido)");
                var nonceResponse = createNonce();
                var object2send = {
                    "message": "La operación ha sido realizada ya previamente (nonce repetido)",
                    "nonce": nonceResponse,
                    "hmac": null,
                    "hashType": objectReceived.hashType
                };
                object2send.hmac = createHmac(object2send, nonceResponse, object2send.hashType);
                ws.send(JSON.stringify(object2send));
            } else {        
                nonces.push(objectReceived.nonce);
                if (createHmac(objectReceived.message, objectReceived.nonce, objectReceived.hashType) === objectReceived.hmac) {
                    createReport("serverLog.txt", "La operación se ha realizado correctamente\n"+message+"\n");
                    console.log("La operación se ha realizado correctamente");
                    var nonceResponse = createNonce();
                    var object2send = {
                        "message": "La operación se ha realizado correctamente",
                        "nonce": nonceResponse,
                        "hmac": null,
                        "hashType": objectReceived.hashType
                    };
                    object2send.hmac = createHmac(object2send, nonceResponse, object2send.hashType);
                    ws.send(JSON.stringify(object2send));
                } else {
                    createReport("serverLog.txt", "La operación ha sido interceptada (hmac incorrecto)\n"+message+"\n");
                    console.log("La operación ha sido interceptada (hmac incorrecto)");
                    var nonceResponse = createNonce();
                    var object2send = {
                        "message": "La operación ha sido interceptada (hmac incorrecto)",
                        "nonce": nonceResponse,
                        "hmac": null,
                        "hashType": objectReceived.hashType
                    };
                    object2send.hmac = createHmac(object2send, nonceResponse, object2send.hashType);
                    ws.send(JSON.stringify(object2send));
                }
            }
        });

    });
});
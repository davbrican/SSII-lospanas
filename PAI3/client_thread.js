const WebSocket = require('ws');
const crypto = require('crypto');
const readline = require('readline');
const fs = require('fs');
const { Agent } = require('https');
var nonces = [];
const hashType = ['sha256', 'sha512', 'sha384'];
var hashInput = "0";
var secret = "s";
var sendingMessage = "123123 456456 100";
var attackSimulation = "0";
var cipherSuites = ['TLS_AES_256_GCM_SHA384', 'TLS_CHACHA20_POLY1305_SHA256', 'TLS_AES_128_GCM_SHA256'];


// Function to create Nonce
function createNonce() {
    return crypto.randomBytes(128).toString('hex');
}

// Function to create HMAC
function createHmac(message, nonce, secret) {
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
            console.log(err);
        }
        /* else {
            console.log("Report created");
        }*/
    });
} 

var i = 0;
while (i < 300) { 
    const socket = new WebSocket('wss://localhost:8081', { rejectUnauthorized: false, ciphers: cipherSuites[0] });

    var object2send = {
        message: sendingMessage,
        hmac: "",
        hashType: hashInput,
        nonce: null
    };


    // Connection opened
    socket.addEventListener('open', function (event) {
        object2send.nonce = createNonce();
        object2send.hmac = createHmac(object2send.message, object2send.nonce, secret); // HMAC created by the client
        switch (attackSimulation) {
            // No attack
            case "0":
                createReport("clientLog.txt", "Mensaje enviado sin ataques\n"+JSON.stringify(object2send)+"\n");
                break;
            // MiTM attack: Reply attack
            case "1":
                createReport("clientLog.txt", "Mensaje simulando un ataque de reply\n"+JSON.stringify(object2send)+"\n");
                socket.send(JSON.stringify(object2send)); // Message resent (reply)
                break;
            // MiTM attack: message modification
            case "2":
                var oldObject = object2send;
                object2send.message += "0"; // Message modified by the Man In The Middle
                createReport("clientLog.txt", "Mensaje simulando un ataque de modificación de mensaje\nEnvio Original:\n"+JSON.stringify(oldObject)+"\nEnvio del Man In The Middle:\n"+JSON.stringify(object2send)+"\n");
                break;
            // MiTM attack: message modification with new HMAC
            case "3":
                var messageSplit = object2send.message.split(" ");
                var oldObject = object2send;
                object2send.message = messageSplit[0] + ' 3545331 ' + messageSplit[2]; // Message modified by the Man In The Middle
                object2send.hmac = createHmac(object2send.message, object2send.nonce, "secretomalcreadoporelmaninthemiddle"); // HMAC created by the Man In The Middle
                createReport("clientLog.txt", "Mensaje simulando un ataque de modificación de mensaje con intento de creación de HMAC\nEnvio Original:\n"+JSON.stringify(oldObject)+"\nEnvio del Man In The Middle:\n"+JSON.stringify(object2send)+"\n");
                break;
            default:
                //console.log("No ha elegido ninguna opción correcta")
                process.exit(1);
        }
        socket.send(JSON.stringify(object2send));
    });

    // Listen for messages
    socket.addEventListener('message', function (event) {
        objectReceived = JSON.parse(event.data);
        //console.log('\nMensaje del servidor:\n', objectReceived);
        
        if (nonces.includes(objectReceived.nonce)) {
            createReport("clientLog.txt", "El mensaje del servidor al cliente ya ha sido recibido previamente (nonce repetido)\n");
            //console.log("\nEl mensaje del servidor al cliente ya ha sido recibido previamente (nonce repetido)");
        } else {
            if (objectReceived.hmac === createHmac(objectReceived.message, objectReceived.nonce, secret)) {
                createReport("clientLog.txt", "El mensaje del servidor al cliente ha sido recibido correctamente\n");
                //console.log("\nEl mensaje del servidor al cliente ha sido recibido correctamente");
                nonces.push(objectReceived.nonce);
            } else {
                createReport("clientLog.txt", "El mensaje del servidor al cliente ha sido recibido incorrectamente\n");
                //console.log("\nEl mensaje del servidor al cliente ha sido recibido incorrectamente");
            }
        }
        createReport("clientLog.txt", "Mensaje del servidor:\n"+JSON.stringify(objectReceived)+"\n");
        socket.close();
    });
    console.log("Cliente Nº " + (i+1));
    i++;
}
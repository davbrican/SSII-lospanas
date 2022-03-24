const WebSocket = require('ws');
const crypto = require('crypto');
const readline = require('readline');
const fs = require('fs');

var nonces = [];
const hashType = ['sha256', 'sha512', 'sha384'];
var hashInput;
var secret;
var sendingMessage;
var attackSimulation;



// Function to generate random number
function randomNumber(min, max) {
    return Math.round(Math.random() * (max - min) + min);
}

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
function createHmac(obj, secret) {
    const hmac = crypto.createHmac(hashType[hashInput], secret);
    
    const nonce = createNonce();
    obj.nonce = nonce;

    hmac.update(obj.message + nonce);

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

rl.question('¿Qué función hash quiere utilizar?\n0 => sha256\n1 => sha512\n2 => sha384\n', function (hashInputI) {
    hashInput = hashInputI;
    rl.question('Escriba la clave simétrica\n', function (secretKey) {
        secret = secretKey;
        rl.question('Introduzca los campos Cuenta Origen, Cuenta Destino, Cantidad, separados por espacios:\n', function (message) {
            sendingMessage = message;
            rl.question('Desea simular algún tipo de ataque:\n0 => Ninguno\n1 => Reply\n2 => MiTM (modificación de mensaje)\n3 => MiTM (modificación de mensaje y de HMAC)\n', function (aSim) {
                attackSimulation = aSim;
                rl.close();
            });
        });
    });
});

rl.on('close', function () {    
    // Create WebSocket connection.
    const socket = new WebSocket('ws://localhost:8081');

    var object2send = {
        message: sendingMessage,
        hmac: "",
        hashType: hashInput,
        nonce: null
    };
    
    
    // Connection opened
    socket.addEventListener('open', function (event) {
        switch (attackSimulation) {
            // No attack
            case "0":
                createReport("clientLog.txt", "Mensaje enviado sin ataques\n"+JSON.stringify(object2send)+"\n");
                object2send.hmac = createHmac(object2send, secret);
                socket.send(JSON.stringify(object2send));
                break;
            // MiTM attack: Reply attack
            case "1":
                createReport("clientLog.txt", "Mensaje simulando un ataque de reply\n"+JSON.stringify(object2send)+"\n");
                object2send.hmac = createHmac(object2send, secret);
                socket.send(JSON.stringify(object2send));
                socket.send(JSON.stringify(object2send)); // Message resent (reply)
                break;
            // MiTM attack: message modification
            case "2":
                createReport("clientLog.txt", "Mensaje simulando un ataque de modificación de mensaje\n"+JSON.stringify(object2send)+"\n");
                object2send.hmac = createHmac(object2send, secret);
                object2send.message += "0"; // Message modified by the Man In The Middle
                socket.send(JSON.stringify(object2send));
                break;
            // MiTM attack: message modification with new HMAC
            case "3":
                createReport("clientLog.txt", "Mensaje simulando un ataque de modificación de mensaje con intento de creación de HMAC\n"+JSON.stringify(object2send)+"\n");
                var messageSplit = object2send.message.split(" ");
                object2send.hmac = createHmac(object2send, secret); // HMAC created by the client
                object2send.message = messageSplit[0] + ' 3545331 ' + messageSplit[2]; // Message modified by the Man In The Middle
                object2send.hmac = createHmac(object2send, "secretomalcreadoporelmaninthemiddle"); // HMAC created by the Man In The Middle
                socket.send(JSON.stringify(object2send));
                break;
            default:
                console.log("No ha elegido ninguna opción correcta")
                process.exit(1);
        }
    });
    
    // Listen for messages
    socket.addEventListener('message', function (event) {
        objectReceived = JSON.parse(event.data);
        console.log('\nMensaje del servidor:\n', objectReceived);
        createReport("clientLog.txt", "Mensaje del servidor:\n"+JSON.stringify(objectReceived)+"\n");
        if (nonces.includes(objectReceived.nonce)) {
            console.log("\nEl mensaje del servidor al cliente ya ha sido recibido previamente (nonce repetido)");
        } else {
            nonces.push(objectReceived.nonce);
        }
        socket.close();
    });
});
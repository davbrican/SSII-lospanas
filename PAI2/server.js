const WebSocket = require('ws');
const crypto = require('crypto');
const readline = require('readline');

var nonces = [];
const hashType = ['sha256', 'sha512', 'sha384'];
var secret;


const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.question('Escribe la clave simétrica\n', function (secretKey) {
    secret = secretKey;
    rl.close();
});

rl.on('close', function () {
    // Create WebSocket connection.
    const socket = new WebSocket('ws://localhost:8081');

    // Function to create HMAC
    function createHmac(message, nonce, hashInput) {
        const hmac = crypto.createHmac(hashType[hashInput], secret);
        
        //const nonce = createNonce();
        //object2send.nonce = nonce;

        hmac.update(message + nonce);

        const hex = hmac.digest('hex');
        return hex;
    }



    const wss = new WebSocket.Server({ port: 8081 });

    // Wire up some logic for the connection event (when a client connects) 
    wss.on('connection', function connection(ws) {

        // Wire up logic for the message event (when a client sends something)
        ws.on('message', function incoming(message) {
            objectReceived = JSON.parse(message);
            console.log('received: %s', objectReceived);
            if (nonces.includes(objectReceived.nonce)) {
                ws.send(JSON.stringify({"message": "Nonce already used"}));
            } else {        
                nonces.push(objectReceived.nonce);
                if (createHmac(objectReceived.message, objectReceived.nonce, objectReceived.hashType) === objectReceived.hmac) {
                    ws.send(JSON.stringify({"message": "OK", "nonce": objectReceived.nonce}));
                } else {
                    ws.send(JSON.stringify({"message": "HMAC incorrecto", "nonce": objectReceived.nonce}));
                }
            }
        });

    });
});
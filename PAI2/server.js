const WebSocket = require('ws');
const crypto = require('crypto');
// Create WebSocket connection.
const socket = new WebSocket('ws://localhost:8081');

var nonces = [];
const hashType = ['sha256', 'sha512', 'sha384'];
var hashInput = 0;
const secret = 'clave-simetrica-secreta';

var object2send = {
    message: "34567891 987654 300",
    hmac: "",
    hashType: hashInput,
    nonce: null
};


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
function createHmac(message) {
    const hmac = crypto.createHmac(hashType[hashInput], secret);
    
    const nonce = createNonce();
    object2send.nonce = nonce;

    hmac.update(message + nonce);

    const hex = hmac.digest('hex');
    return hex;
}



const wss = new WebSocket.Server({ port: 8081 });

// Wire up some logic for the connection event (when a client connects) 
wss.on('connection', function connection(ws) {

    // Wire up logic for the message event (when a client sends something)
    ws.on('message', function incoming(message) {
        console.log('received: %s', JSON.parse(message));
        // Send a message
        ws.send(JSON.stringify(JSON.parse(message)));
    });

});
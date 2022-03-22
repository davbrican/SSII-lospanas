const WebSocket = require('ws');
const crypto = require('crypto');
const readline = require('readline');

var nonces = [];
const hashType = ['sha256', 'sha512', 'sha384'];
var hashInput;
var secret;
var sendingMessage;


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
function createHmac(obj) {
    const hmac = crypto.createHmac(hashType[hashInput], secret);
    
    const nonce = createNonce();
    obj.nonce = nonce;

    hmac.update(obj.message + nonce);

    const hex = hmac.digest('hex');
    return hex;
}
    

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.question('¿Qué función hash quiere utilizar?\n0 => sha256\n1 => sha512\n2 => sha384\n', function (hashInputI) {
    hashInput = hashInputI;
    rl.question('Escribe la clave simétrica\n', function (secretKey) {
        secret = secretKey;
        rl.question('Introduce los campos Cuenta Origen, Cuenta Destino, Cantidad, separados por espacios:\n', function (message) {
            sendingMessage = message;
            rl.close();
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
        object2send.hmac = createHmac(object2send);
        socket.send(JSON.stringify(object2send));
    });
    
    // Listen for messages
    socket.addEventListener('message', function (event) {
        objectReceived = JSON.parse(event.data);
        if (nonces.includes(objectReceived.nonce)) {
            console.log("\nNonce already used");
        } else {
            nonces.push(objectReceived.nonce);
            console.log('\nMessage from server: ', objectReceived);
        }
        socket.close();
    });
});
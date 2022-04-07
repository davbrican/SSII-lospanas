const WebSocket = require('ws');
const crypto = require('crypto');
const readline = require('readline');
const fs = require('fs');
const https = require('https')   

const options = {
  cert: fs.readFileSync('./cert.pem'),
  key: fs.readFileSync('./key.pem')
};

const httpsServer = https.createServer(options);

httpsServer.listen(8081, () => console.log('Https Server running on port 8081'));

var nonces = [];
const hashType = ['sha256', 'sha512', 'sha384'];
var secret;

// Function to create a report
function createReport(reportFile, content) {
    reportFile = "./Reports/" + reportFile;
    fs.appendFile(reportFile, "\n"+content, (err) => {
        if (err) {
            console.log(err);
        } else {
            console.log("Report created");
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

    // Function to create HMAC
    function createHmac(message, nonce, hashInput) {
        const hmac = crypto.createHmac(hashType[hashInput], secret);

        hmac.update(message + nonce);

        const hex = hmac.digest('hex');
        return hex;
    }

    function createNonce() {
        return crypto.randomBytes(128).toString('hex');
    }



    const wss = new WebSocket.Server({ 
        server: httpsServer
      });

    // Wire up some logic for the connection event (when a client connects) 
    wss.on('connection', function connection(ws) {

        // Wire up logic for the message event (when a client sends something)
        ws.on('message', function incoming(message) {
            objectReceived = JSON.parse(message);

            let object2send = {
                message: "",
                hmac: "",
                hashType: objectReceived.hashType,
                nonce: createNonce()
            };


            if (nonces.includes(objectReceived.nonce)) {
                createReport("serverLog.txt", "El mensaje recibido es una respuesta a un ataque MiTM\n"+message+"\n");
                object2send.message = "Nonce already used"
            } else {
                nonces.push(objectReceived.nonce);
                if (createHmac(objectReceived.message, objectReceived.nonce, objectReceived.hashType) === objectReceived.hmac) {
                    object2send.message = "OK"
                } else {
                    object2send.message = "HMAC incorrecto"
                }
            }
            createReport("serverLog.txt", object2send.message+"\n"+message+"\n");
            console.log(object2send.message);
            object2send.hmac = createHmac(object2send.message, object2send.nonce, object2send.hashType);
            ws.send(JSON.stringify(object2send));
        });

    });
});
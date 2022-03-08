const { time } = require('console');
const crypto = require('crypto');
const fs = require('fs');
var nodemailer = require('nodemailer');
const { send, env } = require('process');
require('dotenv').config();
const createCSV = require('csv-writer').createObjectCsvWriter;

const filesPath = './files/';
var nonces = []
var challenges = {
    "1": (file) => { return file + "primero" },
    "2": (file) => { return file + "segundo" },
    "3": (file) =>  { return file + "tercero" },
    "4": (file) =>  { return file + "cuarto" },
    "5": (file) =>  { return file + "quinto" }
}

// Variables que establece el usuario al ejecutar el script
const sendMailYesNo = true;
const hashType = ['sha256', 'sha512', 'sha384'];
const secret = 'clave-simetrica-secreta';

// Function to generate random number
function randomNumber(min, max) {
    return Math.round(Math.random() * (max - min) + min);
}

//  Function to create hash
function createHash(file, nonce) {
    let fileBuffer = fs.readFileSync(file);
    const hashSum = crypto.createHash(hashType[0]);

    if(nonce){
        fileBuffer = addNonceToFile(fileBuffer,nonce);
    }
    hashSum.update(fileBuffer);

    const hex = hashSum.digest('hex');

    return hex;
}

function addNonceToFile(file,nonce) {
    return file+nonce;
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
function createHmac(file, token) {
    const fileBuffer = fs.readFileSync(file);
    const challenge = challenges[token](fileBuffer);

    const hmac = crypto.createHmac(hashType[0], secret);
    
    hmac.update(challenge);

    const hex = hmac.digest('hex');
    return hex;
}

//  Function to store files in an array
async function storeFiles() {
    let filesMap = new Map();
    return new Promise((resolve, reject) => {
        fs.readdir(filesPath, (err, files) => {
            if (err) {
                console.log(err);
                return reject(err);
            } else {
                files.forEach(file => {
                    filesMap.set(file, createHash(filesPath + file));
                });
                return resolve(filesMap);
            }
        });
    });
}


// Function to replace contents of a file
function replaceContents(file, replacement, cb) {

    fs.readFile(replacement, (err, contents) => {
        if (err) return cb(err);
        fs.writeFile(file, contents, cb);
    });
    console.log("Restoring contents of file: " + file);
    return "\nRestoring contents of file: " + file;
}

// Function to check file integrity
const checkIntegrity = async (file) => {
    const hash = createHash(filesPath+file.path);
    var text = "";
    if (hash === file.hash) {
        console.log(`${file.path} is OK`);
        text += `\n${file.path} is OK`;
        return [true, text];
    } else {
        console.log(`${file.path} is corrupted`);
        text += `\n${file.path} is corrupted`;
        if(sendMailYesNo) {
            //sendMail();
        }
        // Restore file
        var restoring = await replaceContents(filesPath + file.path, './backupFiles/' + file.path, err => {
            if (err) {
                console.log(err);
            }
        });
        text += restoring;
        return [false, text];
    }
}


// Function to corrupt a random file
function corruptRandomFile(file) {
    fs.readFile(file, (err, data) => {
        if (err) {
            console.log(err);
        } else {
            const random = randomNumber(0, data.length);
            data[random] = randomNumber(0, 255);
            var texto = "";
            fs.writeFile(file, data, (err) => {
                if (err) {
                    console.log(err);
                } else {
                    console.log(`${file} has been corrupted`);
                    texto += `\n${file} has been corrupted`;
                    createReport("./checkingReport.txt", `${file} has been corrupted`);
                }
            });
        }
    });
}


// Function to send a mail to the client
async function sendMail() {
    let transporter = nodemailer.createTransport({
        host: 'smtp-mail.outlook.com',
        port: 587,
        auth: {
            user: process.env.EMAIL,
            pass: process.env.PASSWORD
        }
      });

    
    let mailOptions = {
        from: 'david.brincau@hotmail.com', // sender address
        to: process.env.TO_MAIL, // list of receivers
        subject: "Corrupted file", // Subject line
        text: "A file has been detected to be corrupted while checking system files integrity.", // plain text body
        html: `
        <h1>Beware!</h1>
        <br>
        <p>One of your files is corrupted.</p>`, // html body
      };
      
    console.log("Sending mail...");
    transporter.sendMail(mailOptions, function(error, info){
        if (error) {
            return console.log(error);
        }
        console.log('Message sent: %s', info.messageId);
    });
}

// Function to create a report
function createReport(reportFile, content) {
    fs.readFile(reportFile, (err, data) => {
        if (err) {
            console.log(err);
        } else {
            fs.writeFile(reportFile, data + '\n' + content, (err) => {
                if (err) {
                    console.log(err);
                } else {
                    console.log("\nReport created");
                }
            });
        }
    });
}

// Function to create a report of the system
function createCSVReport(csv, index, okD, corD) {
    csv.writeRecords([
        { id: index, ok: okD, cor: corD }
    ])
    .then(() => { console.log("Report created"); });
}

function checkHmacClient(file,hmac,token){
    const clientHmac = createHmac(file,token);
    if(clientHmac === hmac){
        return true;
    } else {
        return false;
    }
}

// Function client send file to server
function proofOfPossesion(file,fileHash,token,nonce) {
    if(nonces.includes(nonce)){
        console.log("Nonce already used");
        return {success:false,message:"Nonce already used"};
    }
    const hash = createHash(file,nonce);
    if(fileHash === hash){
        nonces.push(nonce);
        console.log(`${file} is OK`);
        hmac = createHmac(file,token);
        const clientCheck = checkHmacClient(file,hmac,token)
        if(clientCheck) return {success:true,hmac:hmac};
        return {success:false,message:"Client HMAC is not OK"};
    } else {
        nonces.push(nonce);
        return {success:false,message:"Hash is not correct"};
    }
}


//  main function
const main = async () => {
    // Create an array of files
    const filesMap = await storeFiles();
    const keysArray = Array.from(filesMap.keys());
    
    var csv = createCSV({
        path: "dataReport.csv",
        append: true,
        header: [
            {id: "id", title: "ID"},
            {id: "ok", title: "OK"},
            {id: "cor", title: "CORRUPTED"}
        ]
    });

    createReport("./corruptReport.txt", "CORRUPT FILES REPORT");
    createReport("./checkingReport.txt", "CHECKING REPORT");
    // Corrupt a random file each randomTime seconds
    let interval = setInterval(function () {
        console.log("\n\nCorrupting files from " + filesPath);
        var textInterval1 = "\n\nCorrupting files from " + filesPath;
        createReport("./corruptReport.txt", textInterval1);
        const randomFilesNumber = randomNumber(1, keysArray.length);
        for (let i = 0; i < randomFilesNumber; i++) {
            const file = filesPath + keysArray[randomNumber(0, keysArray.length)];
            corruptRandomFile(file);
        }
    }, 995);

    var index = 1;
    // Check integrity of each file each second
    let interval2 = setInterval(function () {
        console.log("\n\nChecking integrity of files from " + filesPath);
        var textInterval2 = "\n\nChecking integrity of files from " + filesPath;
        let data = [0,0];
        for (let i = 0; i < keysArray.length; i++) {
            const file = keysArray[i];
            const result = checkIntegrity({ path: file, hash: filesMap.get(file) });
            result.then(res => { 
                res[0] ? data[0]++ : data[1]++;
                textInterval2 += res[1];
                if (data[0] + data[1] == keysArray.length) {
                    createCSVReport(csv, index, data[0], data[1]);
                    index++;
                    console.log("\n\nIntegrity check finished with the following results: \n" + data[0] + " files are OK\n" + data[1] + " files are corrupted\n");
                    textInterval2 += "\n\nIntegrity check finished with the following results: \n" + data[0] + " files are OK\n" + data[1] + " files are corrupted\n";
                    createReport("./checkingReport.txt", textInterval2);
                }
            });

            let nonce = createNonce();
            const fileHash = createHash(filesPath + file,nonce);
            const fileFullPath = filesPath + file;


            const pop = proofOfPossesion(fileFullPath,fileHash,""+3,nonce);
            if(pop.success){
                console.log("HMAC is OK");
                console.log("HMAC: " + pop.hmac);
            }else {
                console.log("HMAC is not OK");
                console.log(pop.message);
            }
        }
    }, 1000);
    


    // Stop both intervals after 10 seconds
    setTimeout(() => {
        clearInterval(interval);
        clearInterval(interval2);
    }, 10000);
}

main();
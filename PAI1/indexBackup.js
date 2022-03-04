const crypto = require('crypto');
const fs = require('fs');
var nodemailer = require('nodemailer');

const filesPath = './files/';
var nonces = []

// Variables que establece el usuario al ejecutar el script
const sendMailYesNo = true;
const hashType = ['sha256'];
const secret = 'GfG';

// Function to generate random number
function randomNumber(min, max) {
    return Math.floor(Math.random() * (max - min));
}

//  Function to create hash
function createHash(file) {
    const fileBuffer = fs.readFileSync(file);
    const hashSum = crypto.createHash(hashType[0]);
    hashSum.update(fileBuffer);

    const hex = hashSum.digest('hex');

    return hex;
}

// Function to create Nonce
function createNonce() {
    let nonce = crypto.randomBytes(128).toString('hex');
    if (nonces.includes(nonce)) {
        return createNonce();
    } else {
        nonces.push(nonce);
        return nonce;
    }
}

// Function to create HMAC
function createHmac(file) {
    let nonce = createNonce();

    const fileBuffer = fs.readFileSync(file);
    const hmac = crypto.createHmac(hashType[0], secret);
    
    hmac.update(fileBuffer);
    hmac.update(nonce);
    hmac.update("SSII");

    const hex = hmac.digest('hex');
    return hex;
}

//  Function to store files in an array
async function storeFiles() {
    let filesArr = [];
    return new Promise((resolve, reject) => {
        fs.readdir(filesPath, (err, files) => {
            if (err) {
                console.log(err);
                return reject(err);
            } else {
                files.forEach(file => {
                    filesArr.push({"path": file, "hash": createHash(filesPath + file)});
                });
                return resolve(filesArr);
            }
        });
    })
}


// Function to replace contents of a file
function replaceContents(file, replacement, cb) {

    fs.readFile(replacement, (err, contents) => {
        if (err) return cb(err);
        fs.writeFile(file, contents, cb);
    });
    console.log("Restoring contents of file: " + file);

}

// Function to check file integrity
const checkIntegrity = async (file) => {
    const hash = createHash(filesPath+file.path)
    if (hash === file.hash) {
        console.log(`${file.path} is OK`);
        return true;
    } else {
        console.log(`${file.path} is corrupted`);
        if(sendMailYesNo) {
            //sendMail();
        }
        // Restore file
        await replaceContents(filesPath + file.path, './backupFiles/' + file.path, err => {
            if (err) {
                console.log(err);
            }
        });
        return false;
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
            fs.writeFile(file, data, (err) => {
                if (err) {
                    console.log(err);
                } else {
                    console.log(`${file} has been corrupted`);
                }
            });
        }
    });
}


async function sendMail() {
    let transporter = nodemailer.createTransport({
        service: 'mailgun',
        auth: {
          user: process.env.EMAIL,
          pass: process.env.PASSWORD,
        },
      });

    
    let info = await transporter.sendMail({
        from: '"SSII-Group-5" <no-reply@ssii.com>', // sender address
        to: "dbrincau@us.es", // list of receivers
        subject: "Corrupted file", // Subject line
        text: "A file has been detected to be corrupted while checking system files integrity.", // plain text body
        html: `
        <h1>Beware!</h1>
        <br>
        <p>One of your files is corrupted.</p>`, // html body
      });
}





// Function client send file to server
function proofOfPossesion(file,hash,secret) {
    //if(hash === file)
}


//  main function
const main = async () => {
    // Create an array of files
    const filesArray = await storeFiles();


    // Corrupt a random file each randomTime seconds
    let interval = setInterval(function () {
        console.log("\n\nCorrupting files from " + filesPath);
        const randomFilesNumber = randomNumber(1, filesArray.length);
        for (let i = 0; i < randomFilesNumber; i++) {
            const file = filesPath + filesArray[randomNumber(0, filesArray.length)].path;
            corruptRandomFile(file);
        }
    }, 1000);

    // Check integrity of each file each second
    let interval2 = setInterval(function () {
        console.log("\n\nChecking integrity of files from " + filesPath);
        let data = [0,0];
        for (let i = 0; i < filesArray.length; i++) {
            const file = filesArray[i];
            const result = checkIntegrity(file);
            result.then(res => { 
                res ? data[0]++ : data[1]++;
                if (data[0] + data[1] == filesArray.length) {
                    console.log("\n\nIntegrity check finished with the following results: \n" + data[0] + " files are OK\n" + data[1] + " files are corrupted\n");
                }
            });

            const fileHash = createHash(filesPath + file.path);
            const fileFullPath = filesPath + file.path;
            const pop = proofOfPossesion(fileFullPath,fileHash,secret);
        }
    }, 1000);
    


    // Stop both intervals after 10 seconds
    setTimeout(() => {
        clearInterval(interval);
        clearInterval(interval2);
    }, 10000);
}


main(); // TODO Crear TXT con la información de la consola con timestamp y otro archivo CSV con información numérica de lo que ocurre (Nº archivos OK, Nº de archivos corruptos, Nº de archivos corruptos, etc.)
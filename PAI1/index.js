const crypto = require('crypto');
const fs = require('fs');

var filesArray = [];
const filesPath = './files/'

function createHash(file) {
    const fileBuffer = fs.readFileSync(file);
    const hashSum = crypto.createHash('sha256');
    hashSum.update(fileBuffer);

    const hex = hashSum.digest('hex');

    return hex;
}

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

const main = async () => {
    // Create an array of files
    const filesArray = await storeFiles();
    
    // Function to check file integrity
    filesArray.forEach(file => {
        checkIntegrity(file);
    });
}

const checkIntegrity = (file) => {
    const hash = createHash(filesPath+file.path)
    if (hash === file.hash) {
        console.log(`${file.path} is OK`);
    } else {
        console.log(`${file.path} is corrupted`);
    }
}



main();
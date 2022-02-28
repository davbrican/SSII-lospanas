const crypto = require('crypto');
const fs = require('fs');


const filesPath = './files/'

// Function to generate random number
function randomNumber(min, max) {
    return Math.floor(Math.random() * (max - min));
}

//  Function to create hash
function createHash(file) {
    const fileBuffer = fs.readFileSync(file);
    const hashSum = crypto.createHash('sha256');
    hashSum.update(fileBuffer);

    const hex = hashSum.digest('hex');

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

// Function to check file integrity
const checkIntegrity = (file) => {
    const hash = createHash(filesPath+file.path)
    if (hash === file.hash) {
        console.log(`${file.path} is OK`);
    } else {
        console.log(`${file.path} is corrupted`);
        // TODO Restore file
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
                    console.log(`${file} is corrupted`);
                }
            });
        }
    });
}




//  main function
const main = async () => {
    // Create an array of files
    const filesArray = await storeFiles();
    

    // Check integrity of each file each second
    var interval = setInterval(function () {
        console.log("\n\nChecking integrity of file: " + filesPath);
        filesArray.forEach(file => {
            checkIntegrity(file);
        })
    }, 1000);
    
    // Random time for interval2
    var randomTime = randomNumber(1500, 3000);
    // Corrupt a random file each randomTime seconds
    var interval2 = setInterval(function () {
        var file = filesPath + filesArray[randomNumber(0, filesArray.length)].path;
        console.log("\n\nCorrupting file: " + file);
        corruptRandomFile(file);
    }, randomTime);


    // Stop both intervals after 10 seconds
    setTimeout(() => {
        clearInterval(interval);
    }, 10000);
    
    setTimeout(() => {
        clearInterval(interval2);
    }, 10000);
}



main(); // TODO Crear TXT con la información de la consola con timestamp y otro archivo CSV con información numérica de lo que ocurre (Nº archivos OK, Nº de archivos corruptos, Nº de archivos corruptos, etc.)
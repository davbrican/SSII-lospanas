const crypto = require('crypto');
const fs = require('fs');

const main = () => {
    // Function to check file integrity
    checkIntegrity(__dirname + '/files/csv0.csv');

}

const checkIntegrity = (file) => {

    const fileBuffer = fs.readFileSync(file);
    const hashSum = crypto.createHash('sha256');
    hashSum.update(fileBuffer);

    const hex = hashSum.digest('hex');

    console.log(hex);
}

main();
const exec = require('child_process').exec;

function log(error, stdout, stderr) {
    console.log(stdout);
    console.log(stderr);
}

const os = require('os');

const webappPath = "../src/main/webapp";

if (os.type() === 'Darwin') {
    exec(`rm -r "${webappPath}"/*`, log);
    exec(`mv dist/* "${webappPath}"`, log);
} else if (os.type() === 'Windows_NT') {
    exec(`RMDIR "${webappPath}"/*`, log);
    exec(`MOVE /Y dist\\* "${webappPath}"`, log);
} else {
    throw new Error("Unsupported OS found: " + os.type());
}

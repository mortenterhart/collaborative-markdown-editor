const exec = require('child_process').exec;

function log(error, stdout, stderr) {
    console.log(stdout);
    console.log(stderr);
}

const os = require('os');

const webappPath = "../src/main/webapp";

if (os.type() === 'Darwin' || os.type() === 'Linux') {
    exec(`rm -r "${webappPath}"/*`, log);
    exec(`mv dist/* "${webappPath}"`, log);
} else if (os.type() === 'Windows_NT') {
    exec(`del /S /Q "${webappPath}"\\*`, log);
    exec(`xcopy dist\\* "${webappPath}"\\* /s /e /i /Y`, log);
} else {
    throw new Error("Unsupported OS found: " + os.type());
}

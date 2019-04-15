const exec = require('child_process').exec;
function log(error, stdout, stderr) {
    console.log(stdout);
}

const os = require('os');
// control OS
// then run command depending on the OS

if (os.type() === 'Darwin') {
    exec("mv dist/* ../src/main/webapp", log);
} else if (os.type() === 'Windows_NT') {
    exec("xcopy dist\\* ..\\src\\main\\webapp\\* /s /e /i /Y", log);
} else {
    throw new Error("Unsupported OS found: " + os.type());
}

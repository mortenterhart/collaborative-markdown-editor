var sys = require('sys')
var exec = require('child_process').exec;
function puts(error, stdout, stderr) { sys.puts(stdout) }

var os = require('os');
//control OS
//then run command depengin on the OS

if (os.type() === 'MacOS')
    exec("mv dist\\* ../src/main/webapp", puts);
else if (os.type() === 'Windows_NT')
    exec("xcopy dist\\* ..\\src\\main\\webapp\\* /s /e /i /Y", puts);
else
    throw new Error("Unsupported OS found: " + os.type());
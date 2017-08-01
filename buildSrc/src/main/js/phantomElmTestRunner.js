var page = require("webpage").create();
var system = require("system");
var htmlRunnerPath = system.args[1];

page.open("http://localhost:8000/" + htmlRunnerPath, function () {
    var attempts = 0;

    var checkForSuccess = function () {
        if (page.content.indexOf("Test Run Passed") > -1) {
            console.log("Test Run Passed");
            phantom.exit();
        }

        if (attempts >= 10) {
            console.log("Test Run Failed");
            phantom.exit(1);
        }

        attempts++;
        setTimeout(checkForSuccess, 200);
    };

    setTimeout(checkForSuccess, 200);
});


const cors = require('cors');

export function loadJSON(method, url, callback, failureCallback, body = null, headerName = null, headerValue = null) {
    let req = new XMLHttpRequest();
    cors({credentials: true, origin: true, exposedHeaders: '*'})

    req.overrideMimeType("applicationp/json");

    try {
        req.open(method, url, true);
    } catch(err) {
        //document.getElementById("auth_message").innerText= 'Wrong credentials!';
    }

    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            callback(req.responseText);
        } else if ( req.status !== 200) {
            failureCallback(req.status);
        }
    };
    //req.setRequestHeader('Authentithication', `Bearer: ${token}`)
    if(headerName != null && headerValue != null) {
        req.setRequestHeader(headerName, `${headerValue}`)
    }
    req.send(body);
    return req
}
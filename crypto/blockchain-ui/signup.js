
import jsCookie from "js-cookie";
import { loadJSON } from './requests.js';

const login = document.getElementById('signup_button')
const input = document.getElementById('nodeUrl')
const passwordInput = document.getElementById('password')

login.addEventListener('click', () => {

    const url = input.value ;
    const password = passwordInput.value;
    jsCookie.set('nodeUrl', url);

    const body = JSON.stringify({username: url, password: password});
    const req = loadJSON('POST', url + '/' + 'signup', function (response) {
        console.log(response)
    }, function (status) {}, body);

    req.addEventListener("load", function(data) {
        const token =  req.getResponseHeader("Access-token");
        console.log("header:",token);
        jsCookie.set('token', token);
        if(token != null) {
            window.location.replace("index.html");
        } else {
            const authMessage = document.getElementById('auth_message')
            authMessage.innerText = "You already have an account!"

        }
    });


})


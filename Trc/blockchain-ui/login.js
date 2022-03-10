import jsCookie from "js-cookie";

const login = document.getElementById('login_button')
const input = document.getElementById('nodeUrl')

login.addEventListener('click', () => {

    const url = input.value;
    jsCookie.set('nodeUrl', url)


   window.location.replace("index.html");

})

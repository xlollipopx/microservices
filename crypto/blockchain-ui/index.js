import jsCookie from "js-cookie";

import { loadJSON } from './requests.js';

import axios from './lib/axios'

let requestURL

const list = document.querySelector('.list')
const balance = document.querySelector('.balance_num')

function searchUrlCallback(response)  {
    console.log("RESPONSE: " + response );
    const blocks = response //JSON.parse(response.blocks).reverse();
    console.log(blocks.length);
    console.log("cookies: " +  jsCookie.get('nodeUrl') + "))");

    for(let key in blocks) {
        list.innerHTML +=
            `<li>
               <h6>${blocks[key].index}</h6>
               <h6>${blocks[key].timestamp}</h6>  
               <h6>${blocks[key].data}</h6>  
                 ${blocks[key].hash} `
        const trs =  JSON.stringify(blocks[key].transactions);
        for(let t in trs) {
            list.innerHTML+= `${trs[t]} `
        }
        list.innerHTML += `</li>`
    }
}


function searchURL() {
    let url = jsCookie.get('nodeUrl') + '/' + 'blocks';
    console.log(url);
    requestURL = url
    list.innerHTML = ``

    axios.get(url, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": jsCookie.get('token'),
            "Access-Control-Expose-Headers": "*"
        }
    })
        .then(res => res.data)
        .then(data => searchUrlCallback(data.blocks))


    // loadJSON('GET', url,
    //     function (response) {
    //         let blocks = JSON.parse(response).reverse();
    //         console.log(blocks.length);
    //         console.log("cookies: " +  jsCookie.get('nodeUrl') + "))");
    //
    //         let key;
    //         for(key in blocks) {
    //
    //             list.innerHTML +=
    //                 `<li>
    //            <h6>${blocks[key].index}</h6>
    //            <h6>${blocks[key].timestamp}</h6>
    //            <h6>${blocks[key].data}</h6>
    //              ${blocks[key].hash} `
    //
    //
    //            const trs =  JSON.stringify(blocks[key].transactions);
    //             let t;
    //             for(t in trs) {
    //                 list.innerHTML+= `${trs[t]} `
    //             }
    //             list.innerHTML += `</li>`
    //         }
    // },
    //     function (status) {},
    //     null, "Authorization", jsCookie.get('token'));
}

//window.location.replace("signin.html")

searchURL()
console.log(jsCookie.get('token') + " lskmlskmmmmrlk")


const getBalance = () => {
    const address = jsCookie.get('nodeUrl');
    const url = jsCookie.get('nodeUrl') + '/' + 'balance';

    axios.post(url, {
        body: address,
        headers: {
            "Content-Type": "application/json",
            "Authorization":  jsCookie.get('token'),
            "Access-Control-Allow-Origin": "*",
           "Access-Control-Expose-Headers": "*"
        }
    })
        .then((res) => res.data)
        .then(data => {
            balance.innerHTML = `${data}`
        })
        .catch(e => {
            console.log(e)
        })

    /*
    loadJSON('POST', url,
        function (response) {
            balance.innerHTML = `${response.slice(1, response.length - 1)}`;
        },
        function (status) {

        }, address, "Authorization", jsCookie.get('token'));
*/
}

getBalance()
// window.location.replace("signin.html")



const openPopUp = document.getElementById('open_pop_up')
const closePopUp = document.getElementById('pop_up_close')
const popUp = document.getElementById('pop_up')

const userInput = document.getElementById('username')
const urlInput = document.getElementById('url')
const popUpMenuButton = document.getElementById('pop_up_menu_button')


openPopUpEvent(openPopUp, popUp)

closePopUpEvent(closePopUp, popUp)

addEventListenerToMineForm(popUpMenuButton, userInput, urlInput, popUp, 'mineBlock')

const openPopUpPeer = document.getElementById('open_pop_up_peer')
const closePopUpPeer = document.getElementById('pop_up_close_peer')
const popUpPeer = document.getElementById('pop_up_peer')

const userInputPeer = document.getElementById('username_peer')
const urlInputPeer = document.getElementById('url_peer')
const popUpMenuButtonPeer = document.getElementById('pop_up_menu_button_peer')


openPopUpEvent(openPopUpPeer, popUpPeer)

closePopUpEvent(closePopUpPeer, popUpPeer)

addEventListenerToForm(popUpMenuButtonPeer, userInputPeer, urlInputPeer, popUpPeer, 'addPeer')


const openPopUpTransaction = document.getElementById('make_transaction')
const closePopUpTransaction = document.getElementById('pop_up_close_send')
const popUpTransaction = document.getElementById('pop_up_transaction')

const receiverInputTransaction = document.getElementById('receiver')
const amountInputTransaction = document.getElementById('amount')
const popUpMenuButtonTransaction = document.getElementById('pop_up_menu_button_send')

openPopUpEvent(openPopUpTransaction, popUpTransaction)

closePopUpEvent(closePopUpTransaction, popUpTransaction)

addEventListenerToTransForm(popUpMenuButtonTransaction, receiverInputTransaction, amountInputTransaction, popUpTransaction, 'makeTransaction')



function openPopUpEvent(open, popup) {
    open.addEventListener('click', function (e) {
        e.preventDefault()
        popup.classList.add('active');
    })

}

function closePopUpEvent(close, popup) {
    close.addEventListener('click', () => {
        popup.classList.remove('active')
    })

}

function addEventListenerToForm(button, inputOne, inputTwo, popUpOption, command) {
    button.addEventListener('click', () => {
        const peer = {peer: inputOne.value};
        const url = jsCookie.get('nodeUrl') + '/' + command;

        axios.post(url, {
            body: peer,
            headers: {
                Authorization: jsCookie.get('token'),
            }
        })

        /*
        loadJSON('POST',
            url,
            function (response) {},
            function (status) {}, username, "Authorization", jsCookie.get('token'))

         */
        popUpOption.classList.remove('active')
    })
}

function addEventListenerToMineForm(button, inputOne, inputTwo, popUpOption, command) {
    button.addEventListener('click', () => {
        const username = jsCookie.get('nodeUrl');
        const url = jsCookie.get('nodeUrl') + '/' + command;

        axios.post(url, {
            body: username,
            headers: {
                Authorization: jsCookie.get("token")
            }
        })
            .then(res => res.data)
            .then(searchURL)
            .catch(e => console.log(e))
        /*
        loadJSON('POST',
            url,
            searchURL(),
            function (status) {}, username, "Authorization", jsCookie.get('token'))

         */
        popUpOption.classList.remove('active');
    })
}


function addEventListenerToTransForm(button, inputOne, inputTwo, popUpOption, command) {
    button.addEventListener('click', () => {
        const receiver = inputOne.value;
        const amount = inputTwo.value;
        const url = jsCookie.get('nodeUrl') + '/' + command;
        const currentDate = new Date();
        const body = {sender: jsCookie.get('nodeUrl').trim(), receiver: receiver, amount: parseInt(amount), timestamp: currentDate.getTime()}

        axios.post(url, {
            body,
            headers: {
                Authorization: jsCookie.get("token")
            }
        })
            .then(res => res.data)
            .then(data => console.log('[addEventListenerToTransForm]:', data))
        /*
        loadJSON('POST',
            url,
            function (response) {},
            function (status) {}, body, "Authorization", jsCookie.get('token'))
        */
        popUpOption.classList.remove('active')
    })
}







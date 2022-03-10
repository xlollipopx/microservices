import jsCookie from "js-cookie";

let requestURL

//const searchTxt = document.querySelector('#nodeUrl')


function searchURL() {
    let url = jsCookie.get('nodeUrl') + '/' + 'blocks';
    console.log(url);
    requestURL = url
    let list = document.querySelector('.list')
     list.innerHTML = ``

    loadJSON('GET', url,
        function (response) {
        let blocks = JSON.parse(response).reverse();
       console.log(blocks.length);
       console.log("cookies: " +  jsCookie.get('nodeUrl') + "))");

            let key;
            for(key in blocks) {

                list.innerHTML +=
                    `<li>
               <h4>${blocks[key].index}</h4>  
               <h4>${blocks[key].timestamp}</h4>  
               <h4>${blocks[key].data}</h4>  
               <h4>${blocks[key].hash}</h4> `


               const trs =  JSON.stringify(blocks[key].transactions);
                let t;
                for(t in trs) {
                    list.innerHTML+= `${trs[t]} `
                }
                list.innerHTML += `</li>`
            }
    },
        function (status) {});
}


document.getElementById('show_blocks').addEventListener("click", () => searchURL());

function loadJSON(method, url, callback, failureCallback, body = null) {
    let req = new XMLHttpRequest();
    req.overrideMimeType("applicationp/json");

    req.open(method, url, true);

    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            callback(req.responseText);
        } else if ( req.status !== 200) {
            failureCallback(req.status);
        }
    };
    req.send(body);
}


const openPopUp = document.getElementById('open_pop_up')
const closePopUp = document.getElementById('pop_up_close')
const popUp = document.getElementById('pop_up')

const userInput = document.getElementById('username')
const urlInput = document.getElementById('url')
const popUpMenuButton = document.getElementById('pop_up_menu_button')


openPopUpEvent(openPopUp, popUp)

closePopUpEvent(closePopUp, popUp)

addEventListenerToForm(popUpMenuButton, userInput, urlInput, popUp, 'mineBlock')

const openPopUpPeer = document.getElementById('open_pop_up_peer')
const closePopUpPeer = document.getElementById('pop_up_close_peer')
const popUpPeer = document.getElementById('pop_up_peer')

const userInputPeer = document.getElementById('username_peer')
const urlInputPeer = document.getElementById('url_peer')
const popUpMenuButtonPeer = document.getElementById('pop_up_menu_button_peer')


openPopUpEvent(openPopUpPeer, popUpPeer)

closePopUpEvent(closePopUpPeer, popUpPeer)

addEventListenerToForm(popUpMenuButtonPeer, userInputPeer, urlInputPeer, popUpPeer, 'addPeer')


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
        const username = inputOne.value;
        const url = jsCookie.get('nodeUrl') + '/' + command;
        loadJSON('POST',
            url,
            function (response) {},
            function (status) {}, username)
        popUpOption.classList.remove('active')
    })
}








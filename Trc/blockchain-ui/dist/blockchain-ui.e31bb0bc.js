// modules are defined as an array
// [ module function, map of requires ]
//
// map of requires is short require name -> numeric require
//
// anything defined in a previous bundle is accessed via the
// orig method which is the require for previous bundles
parcelRequire = (function (modules, cache, entry, globalName) {
  // Save the require from previous bundle to this closure if any
  var previousRequire = typeof parcelRequire === 'function' && parcelRequire;
  var nodeRequire = typeof require === 'function' && require;

  function newRequire(name, jumped) {
    if (!cache[name]) {
      if (!modules[name]) {
        // if we cannot find the module within our internal map or
        // cache jump to the current global require ie. the last bundle
        // that was added to the page.
        var currentRequire = typeof parcelRequire === 'function' && parcelRequire;
        if (!jumped && currentRequire) {
          return currentRequire(name, true);
        }

        // If there are other bundles on this page the require from the
        // previous one is saved to 'previousRequire'. Repeat this as
        // many times as there are bundles until the module is found or
        // we exhaust the require chain.
        if (previousRequire) {
          return previousRequire(name, true);
        }

        // Try the node require function if it exists.
        if (nodeRequire && typeof name === 'string') {
          return nodeRequire(name);
        }

        var err = new Error('Cannot find module \'' + name + '\'');
        err.code = 'MODULE_NOT_FOUND';
        throw err;
      }

      localRequire.resolve = resolve;
      localRequire.cache = {};

      var module = cache[name] = new newRequire.Module(name);

      modules[name][0].call(module.exports, localRequire, module, module.exports, this);
    }

    return cache[name].exports;

    function localRequire(x){
      return newRequire(localRequire.resolve(x));
    }

    function resolve(x){
      return modules[name][1][x] || x;
    }
  }

  function Module(moduleName) {
    this.id = moduleName;
    this.bundle = newRequire;
    this.exports = {};
  }

  newRequire.isParcelRequire = true;
  newRequire.Module = Module;
  newRequire.modules = modules;
  newRequire.cache = cache;
  newRequire.parent = previousRequire;
  newRequire.register = function (id, exports) {
    modules[id] = [function (require, module) {
      module.exports = exports;
    }, {}];
  };

  var error;
  for (var i = 0; i < entry.length; i++) {
    try {
      newRequire(entry[i]);
    } catch (e) {
      // Save first error but execute all entries
      if (!error) {
        error = e;
      }
    }
  }

  if (entry.length) {
    // Expose entry point to Node, AMD or browser globals
    // Based on https://github.com/ForbesLindesay/umd/blob/master/template.js
    var mainExports = newRequire(entry[entry.length - 1]);

    // CommonJS
    if (typeof exports === "object" && typeof module !== "undefined") {
      module.exports = mainExports;

    // RequireJS
    } else if (typeof define === "function" && define.amd) {
     define(function () {
       return mainExports;
     });

    // <script>
    } else if (globalName) {
      this[globalName] = mainExports;
    }
  }

  // Override the current require with this new one
  parcelRequire = newRequire;

  if (error) {
    // throw error from earlier, _after updating parcelRequire_
    throw error;
  }

  return newRequire;
})({"node_modules/js-cookie/dist/js.cookie.js":[function(require,module,exports) {
var define;
var global = arguments[3];
function _typeof(obj) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (obj) { return typeof obj; } : function (obj) { return obj && "function" == typeof Symbol && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }, _typeof(obj); }

/*! js-cookie v3.0.1 | MIT */
;

(function (global, factory) {
  (typeof exports === "undefined" ? "undefined" : _typeof(exports)) === 'object' && typeof module !== 'undefined' ? module.exports = factory() : typeof define === 'function' && define.amd ? define(factory) : (global = global || self, function () {
    var current = global.Cookies;
    var exports = global.Cookies = factory();

    exports.noConflict = function () {
      global.Cookies = current;
      return exports;
    };
  }());
})(this, function () {
  'use strict';
  /* eslint-disable no-var */

  function assign(target) {
    for (var i = 1; i < arguments.length; i++) {
      var source = arguments[i];

      for (var key in source) {
        target[key] = source[key];
      }
    }

    return target;
  }
  /* eslint-enable no-var */

  /* eslint-disable no-var */


  var defaultConverter = {
    read: function read(value) {
      if (value[0] === '"') {
        value = value.slice(1, -1);
      }

      return value.replace(/(%[\dA-F]{2})+/gi, decodeURIComponent);
    },
    write: function write(value) {
      return encodeURIComponent(value).replace(/%(2[346BF]|3[AC-F]|40|5[BDE]|60|7[BCD])/g, decodeURIComponent);
    }
  };
  /* eslint-enable no-var */

  /* eslint-disable no-var */

  function init(converter, defaultAttributes) {
    function set(key, value, attributes) {
      if (typeof document === 'undefined') {
        return;
      }

      attributes = assign({}, defaultAttributes, attributes);

      if (typeof attributes.expires === 'number') {
        attributes.expires = new Date(Date.now() + attributes.expires * 864e5);
      }

      if (attributes.expires) {
        attributes.expires = attributes.expires.toUTCString();
      }

      key = encodeURIComponent(key).replace(/%(2[346B]|5E|60|7C)/g, decodeURIComponent).replace(/[()]/g, escape);
      var stringifiedAttributes = '';

      for (var attributeName in attributes) {
        if (!attributes[attributeName]) {
          continue;
        }

        stringifiedAttributes += '; ' + attributeName;

        if (attributes[attributeName] === true) {
          continue;
        } // Considers RFC 6265 section 5.2:
        // ...
        // 3.  If the remaining unparsed-attributes contains a %x3B (";")
        //     character:
        // Consume the characters of the unparsed-attributes up to,
        // not including, the first %x3B (";") character.
        // ...


        stringifiedAttributes += '=' + attributes[attributeName].split(';')[0];
      }

      return document.cookie = key + '=' + converter.write(value, key) + stringifiedAttributes;
    }

    function get(key) {
      if (typeof document === 'undefined' || arguments.length && !key) {
        return;
      } // To prevent the for loop in the first place assign an empty array
      // in case there are no cookies at all.


      var cookies = document.cookie ? document.cookie.split('; ') : [];
      var jar = {};

      for (var i = 0; i < cookies.length; i++) {
        var parts = cookies[i].split('=');
        var value = parts.slice(1).join('=');

        try {
          var foundKey = decodeURIComponent(parts[0]);
          jar[foundKey] = converter.read(value, foundKey);

          if (key === foundKey) {
            break;
          }
        } catch (e) {}
      }

      return key ? jar[key] : jar;
    }

    return Object.create({
      set: set,
      get: get,
      remove: function remove(key, attributes) {
        set(key, '', assign({}, attributes, {
          expires: -1
        }));
      },
      withAttributes: function withAttributes(attributes) {
        return init(this.converter, assign({}, this.attributes, attributes));
      },
      withConverter: function withConverter(converter) {
        return init(assign({}, this.converter, converter), this.attributes);
      }
    }, {
      attributes: {
        value: Object.freeze(defaultAttributes)
      },
      converter: {
        value: Object.freeze(converter)
      }
    });
  }

  var api = init(defaultConverter, {
    path: '/'
  });
  /* eslint-enable no-var */

  return api;
});
},{}],"index.js":[function(require,module,exports) {
"use strict";

var _jsCookie = _interopRequireDefault(require("js-cookie"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var requestURL; //const searchTxt = document.querySelector('#nodeUrl')

function searchURL() {
  var url = _jsCookie.default.get('nodeUrl') + '/' + 'blocks';
  console.log(url);
  requestURL = url;
  var list = document.querySelector('.list');
  list.innerHTML = "";
  loadJSON('GET', url, function (response) {
    var blocks = JSON.parse(response).reverse();
    console.log(blocks.length);
    console.log("cookies: " + _jsCookie.default.get('nodeUrl') + "))");
    var key;

    for (key in blocks) {
      list.innerHTML += "<li>\n               <h4>".concat(blocks[key].index, "</h4>  \n               <h4>").concat(blocks[key].timestamp, "</h4>  \n               <h4>").concat(blocks[key].data, "</h4>  \n               <h4>").concat(blocks[key].hash, "</h4> ");
      var trs = JSON.stringify(blocks[key].transactions);
      var t = void 0;

      for (t in trs) {
        list.innerHTML += "".concat(trs[t], " ");
      }

      list.innerHTML += "</li>";
    }
  }, function (status) {});
}

document.getElementById('show_blocks').addEventListener("click", function () {
  return searchURL();
});

function loadJSON(method, url, callback, failureCallback) {
  var body = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : null;
  var req = new XMLHttpRequest();
  req.overrideMimeType("applicationp/json");
  req.open(method, url, true);

  req.onreadystatechange = function () {
    if (req.readyState === 4 && req.status === 200) {
      callback(req.responseText);
    } else if (req.status !== 200) {
      failureCallback(req.status);
    }
  };

  req.send(body);
}

var openPopUp = document.getElementById('open_pop_up');
var closePopUp = document.getElementById('pop_up_close');
var popUp = document.getElementById('pop_up');
var userInput = document.getElementById('username');
var urlInput = document.getElementById('url');
var popUpMenuButton = document.getElementById('pop_up_menu_button');
openPopUpEvent(openPopUp, popUp);
closePopUpEvent(closePopUp, popUp);
addEventListenerToForm(popUpMenuButton, userInput, urlInput, popUp, 'mineBlock');
var openPopUpPeer = document.getElementById('open_pop_up_peer');
var closePopUpPeer = document.getElementById('pop_up_close_peer');
var popUpPeer = document.getElementById('pop_up_peer');
var userInputPeer = document.getElementById('username_peer');
var urlInputPeer = document.getElementById('url_peer');
var popUpMenuButtonPeer = document.getElementById('pop_up_menu_button_peer');
openPopUpEvent(openPopUpPeer, popUpPeer);
closePopUpEvent(closePopUpPeer, popUpPeer);
addEventListenerToForm(popUpMenuButtonPeer, userInputPeer, urlInputPeer, popUpPeer, 'addPeer');

function openPopUpEvent(open, popup) {
  open.addEventListener('click', function (e) {
    e.preventDefault();
    popup.classList.add('active');
  });
}

function closePopUpEvent(close, popup) {
  close.addEventListener('click', function () {
    popup.classList.remove('active');
  });
}

function addEventListenerToForm(button, inputOne, inputTwo, popUpOption, command) {
  button.addEventListener('click', function () {
    var username = inputOne.value;
    var url = _jsCookie.default.get('nodeUrl') + '/' + command;
    loadJSON('POST', url, function (response) {}, function (status) {}, username);
    popUpOption.classList.remove('active');
  });
}
},{"js-cookie":"node_modules/js-cookie/dist/js.cookie.js"}],"node_modules/parcel-bundler/src/builtins/hmr-runtime.js":[function(require,module,exports) {
var global = arguments[3];
var OVERLAY_ID = '__parcel__error__overlay__';
var OldModule = module.bundle.Module;

function Module(moduleName) {
  OldModule.call(this, moduleName);
  this.hot = {
    data: module.bundle.hotData,
    _acceptCallbacks: [],
    _disposeCallbacks: [],
    accept: function (fn) {
      this._acceptCallbacks.push(fn || function () {});
    },
    dispose: function (fn) {
      this._disposeCallbacks.push(fn);
    }
  };
  module.bundle.hotData = null;
}

module.bundle.Module = Module;
var checkedAssets, assetsToAccept;
var parent = module.bundle.parent;

if ((!parent || !parent.isParcelRequire) && typeof WebSocket !== 'undefined') {
  var hostname = "" || location.hostname;
  var protocol = location.protocol === 'https:' ? 'wss' : 'ws';
  var ws = new WebSocket(protocol + '://' + hostname + ':' + "33257" + '/');

  ws.onmessage = function (event) {
    checkedAssets = {};
    assetsToAccept = [];
    var data = JSON.parse(event.data);

    if (data.type === 'update') {
      var handled = false;
      data.assets.forEach(function (asset) {
        if (!asset.isNew) {
          var didAccept = hmrAcceptCheck(global.parcelRequire, asset.id);

          if (didAccept) {
            handled = true;
          }
        }
      }); // Enable HMR for CSS by default.

      handled = handled || data.assets.every(function (asset) {
        return asset.type === 'css' && asset.generated.js;
      });

      if (handled) {
        console.clear();
        data.assets.forEach(function (asset) {
          hmrApply(global.parcelRequire, asset);
        });
        assetsToAccept.forEach(function (v) {
          hmrAcceptRun(v[0], v[1]);
        });
      } else if (location.reload) {
        // `location` global exists in a web worker context but lacks `.reload()` function.
        location.reload();
      }
    }

    if (data.type === 'reload') {
      ws.close();

      ws.onclose = function () {
        location.reload();
      };
    }

    if (data.type === 'error-resolved') {
      console.log('[parcel] ✨ Error resolved');
      removeErrorOverlay();
    }

    if (data.type === 'error') {
      console.error('[parcel] 🚨  ' + data.error.message + '\n' + data.error.stack);
      removeErrorOverlay();
      var overlay = createErrorOverlay(data);
      document.body.appendChild(overlay);
    }
  };
}

function removeErrorOverlay() {
  var overlay = document.getElementById(OVERLAY_ID);

  if (overlay) {
    overlay.remove();
  }
}

function createErrorOverlay(data) {
  var overlay = document.createElement('div');
  overlay.id = OVERLAY_ID; // html encode message and stack trace

  var message = document.createElement('div');
  var stackTrace = document.createElement('pre');
  message.innerText = data.error.message;
  stackTrace.innerText = data.error.stack;
  overlay.innerHTML = '<div style="background: black; font-size: 16px; color: white; position: fixed; height: 100%; width: 100%; top: 0px; left: 0px; padding: 30px; opacity: 0.85; font-family: Menlo, Consolas, monospace; z-index: 9999;">' + '<span style="background: red; padding: 2px 4px; border-radius: 2px;">ERROR</span>' + '<span style="top: 2px; margin-left: 5px; position: relative;">🚨</span>' + '<div style="font-size: 18px; font-weight: bold; margin-top: 20px;">' + message.innerHTML + '</div>' + '<pre>' + stackTrace.innerHTML + '</pre>' + '</div>';
  return overlay;
}

function getParents(bundle, id) {
  var modules = bundle.modules;

  if (!modules) {
    return [];
  }

  var parents = [];
  var k, d, dep;

  for (k in modules) {
    for (d in modules[k][1]) {
      dep = modules[k][1][d];

      if (dep === id || Array.isArray(dep) && dep[dep.length - 1] === id) {
        parents.push(k);
      }
    }
  }

  if (bundle.parent) {
    parents = parents.concat(getParents(bundle.parent, id));
  }

  return parents;
}

function hmrApply(bundle, asset) {
  var modules = bundle.modules;

  if (!modules) {
    return;
  }

  if (modules[asset.id] || !bundle.parent) {
    var fn = new Function('require', 'module', 'exports', asset.generated.js);
    asset.isNew = !modules[asset.id];
    modules[asset.id] = [fn, asset.deps];
  } else if (bundle.parent) {
    hmrApply(bundle.parent, asset);
  }
}

function hmrAcceptCheck(bundle, id) {
  var modules = bundle.modules;

  if (!modules) {
    return;
  }

  if (!modules[id] && bundle.parent) {
    return hmrAcceptCheck(bundle.parent, id);
  }

  if (checkedAssets[id]) {
    return;
  }

  checkedAssets[id] = true;
  var cached = bundle.cache[id];
  assetsToAccept.push([bundle, id]);

  if (cached && cached.hot && cached.hot._acceptCallbacks.length) {
    return true;
  }

  return getParents(global.parcelRequire, id).some(function (id) {
    return hmrAcceptCheck(global.parcelRequire, id);
  });
}

function hmrAcceptRun(bundle, id) {
  var cached = bundle.cache[id];
  bundle.hotData = {};

  if (cached) {
    cached.hot.data = bundle.hotData;
  }

  if (cached && cached.hot && cached.hot._disposeCallbacks.length) {
    cached.hot._disposeCallbacks.forEach(function (cb) {
      cb(bundle.hotData);
    });
  }

  delete bundle.cache[id];
  bundle(id);
  cached = bundle.cache[id];

  if (cached && cached.hot && cached.hot._acceptCallbacks.length) {
    cached.hot._acceptCallbacks.forEach(function (cb) {
      cb();
    });

    return true;
  }
}
},{}]},{},["node_modules/parcel-bundler/src/builtins/hmr-runtime.js","index.js"], null)
//# sourceMappingURL=/blockchain-ui.e31bb0bc.js.map
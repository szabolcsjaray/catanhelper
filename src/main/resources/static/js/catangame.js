var running = false;
var playerHash;
var me = null;
var pOrder = new Array();
var myLastState = "JUST_ARRIVED";

var iCB = function() {
  hifeEl("input");
  alert(getEl("inputField").value);
};
var inputCallBack = iCB;

function updateStateAndButtons() {
    if (me==null) {
        return;
    }
    hideEl("startButton");
    hideEl("newPlayerButton");
    hideEl("goOnButton");
    var stateDiv = getEl("state");
    if (me.state=="WAITING_FOR_GAME_START") {
        stateDiv.innerHTML="Ha mindenki belépett, és jó a sorrend, nyomd meg a Start gombot.";
        if (Object.keys(game.players).length>2) {
            showEl("startButton", "inline-block");
        }
        showEl("newPlayerButton", "inline-block");
    } else if (me.state=="GAME_WAITING_FOR_MY_CARD"||
               me.state=="CONFLICT_RESET") {
            if (me.state=="CONFLICT_RESET") {
                stateDiv.innerHTML="Valaki rosszul jelölt, kérlek (ismét) jelöld a saját lapodat!";
            } else {
                stateDiv.innerHTML="Jelöld meg a saját lapodat, amint lent vannak az asztalon a kártyák!";
            }
    } else if (me.state=="GAME_WAITING_FOR_MY_CHOICE") {
         stateDiv.innerHTML="Jelöld meg a lapot" +
         ", amelyikről szerinted a mesélő beszélt!";
    } else if (me.state=="WAITING_FOR_OTHERS_CHOICE") {
        checkOtherPlayers();
        var ready=countReady();
        stateDiv.innerHTML="Megvárjuk, hogy a többiek is bejelöljék a kártyákat... (" +
                           ready + "/" + Object.keys(game.players).length + ")";
    } else if (me.state=="ROUND_ENDED") {
        showEl("goOnButton");
        stateDiv.innerHTML="Kör vége, ha mindenki megnézte az eredményt, akkor valaki nyomja meg a tovább gombot!";
    }
}

function inRoundState(state) {
    return state=="GAME_WAITING_FOR_MY_CHOICE" ||
           state=="GAME_WAITING_FOR_MY_CARD" ||
           state=="CONFLICT_RESET";
}

function findNextPlayer() {
    var nextPlayer = null;
    Object.keys(game.players).forEach( function(pName) {
       if (game.players[pName].deviceHash==deviceHash &&
           inRoundState(game.players[pName].state) &&
           pName!=me.name) {
           nextPlayer=game.players[pName];
       }
    });
    return nextPlayer;
}

function checkOtherPlayers(response) {
    var nextPlayer = findNextPlayer();
    if (nextPlayer!=null) {
        alert("Add át az mobilt/tabletet/laptopot a "+nextPlayer.name+"-nak/nek!\n"+
              "Ha megtörtént, nyomd meg az OK-t");
        window.location.href=myAddress+"game.html?h="+nextPlayer.playerHash;
    }
}

/*function sendMyCard() {
    var mycCardSet = new Object();
    mycCardSet.ownCard = myCard;
    mycCardSet.playerId = me.playerId;
    sendRest("setowncard",doNothing, doNothing, true,mycCardSet);
    return;
}*/

function playerUp(pId) {
    sendRest("playerUp/"+pId,doNothing);
}

function playerDown(pId) {
    sendRest("playerDown/"+pId,doNothing);
}

function updatePlayers() {
    var playerInner = "";
    pOrder.forEach(function(o,i) {
        var pName = Object.keys(game.players)[o];
        var player = game.players[pName];
        var textColor = player.whiteText ? "white" : "black";
        var whitePostFix= player.whiteText ? "_white" : "";
        let gameLogClick = (game.state=="WAITING_FOR_PLAYERS") ? "" : " onClick=\"gameLog();";
        playerInner += "<div style=\"width:45%;background-color:"+ player.colorStr+
                        ";color:"+ textColor +";margin:10px;display:inline-block;"+
                        "text-align:left;" +
                        "border-radius: 8px; border: 1px solid "+ player.colorStr+";" +
                        "line-height:50px;font-weight:bold;\"" + gameLogClick + "\">";
        playerInner += "<div style=\"width:55%;display:inline-block;margin:0;position:relative;"+
                       "padding-left:40px;\">"
        if (pName==me.name) {
            playerInner += getRabbit(40,"black","5px", "0px", "white", "10px");
        } else if (player.deviceHash==me.deviceHash) {
            playerInner += getRabbit(40,"gray","5px", "0px", "white", "10px");
        } else {
            playerInner += "&nbsp; ";
        }
        playerInner += pName;
        if (player.teller) {
            playerInner += " <img src=\"pict/book" + whitePostFix + ".svg\"" +
                           " style=\"width:40px;height:40px;top:5px;position:relative;\"> ";
        }
        playerInner += "</div>";
        playerInner += "<div style=\"text-align:right;width:35%;display:inline-block;margin:0;font-size:30px;\">";
        if (player.state!="WAITING_FOR_GAME_START") {
            if (player.state=="ROUND_ENDED") {
                playerInner += (player.point-player.roundPoint) + "+" +
                               player.roundPoint +"="+player.point+"&nbsp;";
            } else {
                playerInner += player.point+"&nbsp;";
            }
        } else {
            if (i!=0) {
                playerInner += "<img src=\"pict/ArrowUp" + whitePostFix + ".svg\" onClick=\"playerUp("+
                               player.playerId+");\"> &nbsp;";
            }
            if (i<pOrder.length-1) {
                playerInner += "<img src=\"pict/ArrowDown" + whitePostFix + ".svg\" onClick=\"playerDown("+
                               player.playerId+");\"> &nbsp;";
            }
        }
        playerInner += "</div>";
        playerInner += "</div>";
    });
    getEl("players").innerHTML=playerInner;
}

function updateOrder() {
    pOrder = new Array();
    for(var i=0;i<Object.keys(game.players).length;i++) {
        pOrder.push(i);
    }
    pOrder.sort(function(a,b) {
        return (game.players[Object.keys(game.players)[a]].playerOrder - game.players[Object.keys(game.players)[b]].playerOrder);
    });
}

function getMaxPoint() {
    var max = 0;
    Object.keys(game.players).forEach( function(pName) {
        let player = game.players[pName];
        if (player.point>max) {
            max = player.point;
        }
    });
    return max;
}

function updateRabbits() {
    let dRabbits = getEl("rabbits");
    let htmlStr = "";
    let maxPoint = getMaxPoint() + 10;
    var offset = new Map();
    pOrder.forEach(function(o,i) {
        let pName = Object.keys(game.players)[o];
        let player = game.players[pName];
        let myOffset = Math.round(player.point*100/maxPoint);
        if (offset.has(myOffset)) {
            offset.set(myOffset, offset.get(myOffset) + 4);
        } else {
            offset.set(myOffset, 0);
        }
        htmlStr += getRabbit(50, player.colorStr,
                offset.get(myOffset)+"px",
                (myOffset+offset.get(myOffset)/20)+"%",
                "white",
                "10px");
    });
    dRabbits.innerHTML = htmlStr;
}

function updateGame(gameJson) {
    game = JSON.parse(gameJson);
    Object.keys(game.players).forEach(function(pName) {
        if (game.players[pName].playerHash==playerHash) {
            if (me!=null) {
                myLastState=me.state;
            }
            me = game.players[pName];
            getEl("catanHeader").innerHTML="CATANHELPER -- " +
                                           getRabbit(80,me.colorStr,"10px", "10px", "white", "10px", "inline-block") +
                                           pName + " -- kör: " + game.round;
        }
    });
    updateOrder();
    updateStateAndButtons();
    //updateCards();
    updatePlayers();
    //updateRabbits();
}

function checkVersion(version) {
    if (version!=lastVersion) {
        sendRest("getgame", updateGame);
        lastVersion = version;
    }
}

function pollState() {
    if (!running) {
        running = true;
        sendRest("getgameversion", checkVersion)
        running = false;
    }
    setTimeout(function() {pollState();}, 500);
}

function readPlayerFromURL() {
    var url = new URL(window.location.href);
    playerHash = url.searchParams.get("h");
}

function readDeviceHashAndPlayer() {
    readPlayerFromURL();
    deviceHash = getCookie("catanDevice");
    if (deviceHash=="" || playerHash==null) {
        if (confirm("Nincs megadva eszköz azonosító, és/vagy játékos név.\n"+
                    " Javasolt visszatérni a kezdőoldalra. OK?")) {
            window.location.href = myAddress + "start.html";
        } else {
            alert("A játék így nem fog megfelelően működni.\n" +
                  "Olvasd be a kódot, vagy kattints a megjelenített linkre!");
            getEl("state").innerHTML="<a href=\""+myAddress+"start.html\">"+
                                     myAddress+"start.html</a>";
        }
        return false;
    }
    return true;
}

function startSuceeded(response) {
    hideEl("startButton");
    hideEl("newPlayerButton");
}

function startGame() {
    if (confirm("Biztosan mindenki belépett a játékba?")) {
        sendRest("startgame",startSuceeded);
    }
}

function plusPlayer() {
    window.location.href = myAddress+"start.html";
}

function nextRound() {
    sendRest("nextRound",doNothing);
}
function buildField(fieldJson) {
    field = JSON.parse(fieldJson);
    let tilesHTML = "";
    tilesHTML += "<img src=\"pict/field/tenger.png\" style=\"position:absolute;width:1200px;"+
    "height:1000px;top:-400px;left:-600px;opacity:0.7;\">";
    field.gameTiles.forEach(function(tile) {
        tilesHTML += "<img src=\"pict/field/" + tile.type.picture + "\" style=\"position:absolute;"+
        "width:200px;height:200px;"+
        "top:" + (parseInt(tile.y, 10) * 150) + "px;"+
        "left:" + (-100 + parseInt(tile.x, 10) * 200 - (Math.abs(parseInt(tile.y, 10))%2 * 101) ) + "px;\">";
    });
    getEl("map").innerHTML = tilesHTML;
}

function readField() {
    sendRest("getfield", buildField)
}

function inputMinMax(el, min, max) {
    if (el.value.length==0) { return; }
    if (el.value<min) el.value=min;
    if (el.value>max) el.value=el.value.substr(0, el.value.length-1);
}

function input(n) {
    let el = getEl("inputField");
    if (n<0) {
        if (n==-1) {
            if (el.value.length==0) {
                return;
            }
            inputCallBack();
        } else if (n==-2) {
            if (el.value.length>0) {
                el.value=el.value.substr(0, el.value.length-1);
            }
        }
    } else {
        el.value = el.value + n;
        inputMinMax(el, 1, 12);
    }
}


function init() {
    if (readDeviceHashAndPlayer()) {
        pollState();
    }
    readField();
}

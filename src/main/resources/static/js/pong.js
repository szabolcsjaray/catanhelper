var deviceHash;
var version = -1;
var checkOngoing = false;
var players;
var calledBy = [];
var timing = 50;
var bx, by;

const buttonStr = '<button class="gameButton BUTTONSTATE" id="ButtonPLAYERID" onclick="callToPlay(PLAYERID);">JÁTÉK</button>';
const playerLine = '<li class="gamer" id="PLAYERID"><div class="gamerName">NAME</div> <div class="gamerButton">POINT pt '
    + buttonStr + '</div></li>';

function setName() {
    console.log(getEl("name").value + " to send.");
    registerPongPlayer();
}

function startPong() {
    deviceHashPong();
    checkVersion();
}

function checkVersion() {
    sendRest("/version",versionValueCheck, alertIt);
    setTimeout(checkVersion, 5000);
}

function versionValueCheck(versionValue) {
    if (checkOngoing)
        return;

    checkOngoing = true;
    console.log(versionValue);
    if (versionValue!=version) {
        console.log("new version value:" + versionValue);
        version = versionValue;
        readPlayerList();
    }
    checkOngoing = false;
}

function readPlayerList() {
    console.log("sending playerlist...");
    sendRest("/playerlist",newPlayerList, alertIt);
}

function newPlayerList(newPlayers) {
    console.log("playerlist returned...");
    console.log(newPlayers);
    players = JSON.parse(newPlayers);
    let listStr = "";
    players.filter(player => player.device==deviceHash).forEach( me => {
        calledBy = me.calledBy;
    });
    players.forEach(player => {
        let playerStr = playerLine;
        if (player.device==deviceHash) {
            playerStr = playerStr.replace(buttonStr, '');
        } else {
            if (player.calledBy.indexOf(deviceHash)!=-1) {
                playerStr = playerStr.replace("BUTTONSTATE", "calledPlayer");
            } else {
                if (calledBy.indexOf(player.device)!=-1) {
                    playerStr = playerStr.replace("BUTTONSTATE", "calledMe");
                } else {
                    playerStr = playerStr.replace("BUTTONSTATE", "");
                }
            }
        }
        playerStr = playerStr.replace("POINT", player.points)
            .replace("NAME", player.name)
            .replaceAll("PLAYERID", player.device);
        listStr = listStr + playerStr;
    });
    getEl("dynPlayerList").innerHTML = listStr;
}

function callToPlay(calledDevice) {
    let callData = new Object();
    callData.caller = deviceHash;
    callData.called = calledDevice;
    sendRest("/call",calledToPlay, alertIt, true,callData);
}

function calledToPlay() {
    console.log("Other player called to play");
    checkVersion();
}

function deviceHashPong(gameJson) {
    deviceHash = getCookie("pongDevice");
    if (deviceHash=="") {
        deviceHash = 10000 + Math.round(Math.random(2)*89000);
        setCookie("pongDevice", ""+deviceHash, 31);
    }
}

function registerPongPlayer(go = true) {
    let playerEl = getEl("name");
    if (playerEl.value=="")
        return;
    selectedColor = '';
    running = true;
    playerData.deviceHash = deviceHash;
    playerData.playerName = playerEl.value;
    playerData.colorName = selectedColor;
    sendRest("/player",pongPlayerAdded, alertIt, true,playerData);
    running = false;
}

function pongPlayerAdded() {
    console.log("Player name "+getEl("name").value+ " sent to the server successfully.");
    checkVersion();
}

//  PONG game html related functions

function getTiming() {
    sendRest("/timing",pongTimingSet, alertIt);
}

function pongTimingSet(t) {
    timing = t;
}

function startPongGame() {
    deviceHashPong();
    getTiming();
    bx = 40;
    bvx = 0.8*2;
    bvy = 0.4*2;
    by = 40;
    getPos();
    moveBall();
}

function moveBall() {
    setTimeout(moveBall, 50);
    bx +=bvx;
    by +=bvy;
    if (bx<5 || bx>95) {
        bvx = -bvx;
    }
    if (by<5 || by>95) {
        bvy = -bvy;
    }
    getEl("ball").style.left = bx+'%';
    getEl("ball").style.top = by+'%';
}


function getPos() {
    setTimeout(getPos, timing);
    sendRest("/pos",pongPosSet, alertIt);
}

function pongPosSet(pos) {
    let posnum = JSON.parse(pos)
    getEl("p0").style.top = posnum[0]+'%';
    getEl("p1").style.top = posnum[1]+'%';
}
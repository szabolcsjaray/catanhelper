var colorDivContent = "";
var selectedColor = "";
var running = false;
var testMode = window.location.href.indexOf("test");
var myAddress=window.location.href.reverse().substring(10+(testMode>-1 ? 5 : 0)).reverse();
var goToGameFlag = false;
var testPlayers = 0;

function addColorDiv(colStr,colName) {
    colorDivContent += "<div "+
        "style=\"display:inline-block; background-color: "+colStr+";"+
          " width:150px; height:80px; margin: 20px; border: 2px solid #C0C0F0;\" "+
          "colorName=\""+colName+"\" "+
          "onClick=\"selectColor(\'"+colName+"\', this);\" "+
          "id=\""+colName+"\" "+
          ">"+
        "&nbsp;"+
        "</div>";
}

function selectColor(colName, divEl) {
    if (selectedColor!="") {
        document.getElementById(selectedColor).style.borderWidth="2px";
        document.getElementById(selectedColor).style.borderColor=textColor;
    }
    selectedColor=colName;
    divEl.style.borderWidth="10px";
    divEl.style.borderBottomColor=evenDarkerTextColor;
    divEl.style.borderRightColor=darkerTextColor;
}

function initColors(gameJson) {
    game = JSON.parse(gameJson);
    colorDivContent = "";
    Object.keys(game.availableColors).forEach(function(key, index) {addColorDiv(game.availableColors[key], key)});
    document.getElementById("colorsDiv").innerHTML = colorDivContent;
}

function checkVersion(version) {
    if (version!=lastVersion) {
        sendRest("getgame", initColors);
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

function storeDeviceHash(returnedDevHash) {
}

function checkDeviceAndRegisterIfNeeded(gameJson) {
    deviceHash = getCookie("catanDevice");
    if (deviceHash=="") {
        deviceHash = 10000 + Math.round(Math.random(2)*89000);
        setCookie("catanDevice", ""+deviceHash, 31);
    }
    game = JSON.parse(gameJson);
    var foundDevice = false;
    Object.keys(game.devices).forEach(function(deviceKey) {
        if (deviceKey==deviceHash) {
            foundDevice=true;
        }
    });
    if (!foundDevice) {
        sendRest("connect/"+deviceHash,storeDeviceHash);
    }
}

function init() {
    getEl("playerName").value="";
    sendRest("getgame",checkDeviceAndRegisterIfNeeded);
    var url = new URL(window.location.href);
    let test = url.searchParams.get("test");
    if (testMode>-1) {
        showEl("testGame");
    }
    pollState();
}

function getPlayerHash() {
    var pHash;
    Object.keys(game.players).forEach(function(pName) {
        if (pName==playerData.playerName) {
            pHash=game.players[pName].playerHash;
        }
    });
    return pHash;
}

function goToGame(gameJson) {
    game = JSON.parse(gameJson);
    var myHash = getPlayerHash();
    window.location.href = myAddress+"game.html?h="+myHash;
}

function playerAdded() {
    selectedColor="";
    if (goToGameFlag) {
        sendRest("getgame", goToGame);
    } else {
        sendRest("getgame", regTestPlayers);
    }
}

function registerPlayer(go = true) {
    var playerEl = getEl("playerName");
    if (playerEl.value=="")
        return;
    if (selectedColor=="")
        return;
    running = true;
    playerData.deviceHash = deviceHash;
    playerData.playerName = playerEl.value;
    playerData.colorName = selectedColor;
    goToGameFlag = go;
    testPlayers++;
    sendRest("addPlayer",playerAdded, alertIt, true,playerData);
    running = false;
}

function regTestPlayers() {
    if (testPlayers==0) {
        getEl("playerName").value="TestJoe";
        selectedColor = "PINK";
        registerPlayer(false);
    } else if (testPlayers==1) {
        getEl("playerName").value="TestJane";
        selectedColor = "BLUE";
        registerPlayer(false);
    } else if (testPlayers==2) {
        getEl("playerName").value="TestJack";
        selectedColor = "GRAY";
        registerPlayer(true);
    } else {
        goToGame();
    }
}

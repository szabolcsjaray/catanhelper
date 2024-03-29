/*
 * Decompiled with CFR 0.137.
 *
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.ResponseBody
 *  org.springframework.web.bind.annotation.RestController
 */
package com.jeec.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeec.game.Game;
import com.jeec.game.Player;
import com.jeec.game.forms.AddPlayerForm;
import com.jeec.game.forms.SetCardForm;
import com.jeec.game.log.GameLog;

import io.nayuki.qrcodegen.QrCode;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins={"*"})
public class GameController {
    private String ip;
    private Game game = new Game();
    private static boolean testMode=false;

    public static String getIp() {
        String ip;
        try {
            try (DatagramSocket socket = new DatagramSocket();){
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                ip = socket.getLocalAddress().getHostAddress();
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
            ip = e.getMessage();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            ip = e.getMessage();
        }
        return ip;
    }

    @RequestMapping(value={"/"}, method=RequestMethod.GET)
    public String index() {
        String ip = GameController.getIp();
        StringBuilder page = new StringBuilder();
        page.append("<html><head><style> svg {width:500px;}</style></head><body>");
        page.append("Game (Catan, Pong) Helper.<br> \n"+
            " <h1>CATAN</h1><br>"+
            "Nyisd meg ezt a c\u00edmet a jelentkez\u00e9shez: ");
        String address="http://" + ip + ":8080/start.html";
        String testString = (testMode ? "?test" : "");
        page.append("<b><a href=\""+address+testString+"\">"+address+testString+"</a></b>");
        page.append("<br>... vagy olvasd be a QR kódot az induló oldal címét, és úgy nyisd meg!<br><div style='height:200px;'>");
        final int STARTING_XML_HEADER_LENGTH = 137;
        page.append(new String(createQRImage("start.html")).substring(STARTING_XML_HEADER_LENGTH));

        page.append("\n"+
                "</div><h1>PONG</h1><br>"+
                "Nyisd meg ezt a c\u00edmet a jelentkez\u00e9shez: ");
        String addressPong="http://" + ip + ":8080/pongstart.html";
        page.append("<b><a href=\""+addressPong+testString+"\">"+addressPong+testString+"</a></b>");
        page.append("<br>... vagy olvasd be a QR kódot az induló oldal címét, és úgy nyisd meg!<br><div style='height:200px;'>");
        page.append(new String(createQRImage("pongstart.html")).substring(STARTING_XML_HEADER_LENGTH));

        page.append("</div></body></html>");
        return page.toString();
    }

    @RequestMapping(value={"/qrimage"}, method=RequestMethod.GET)
    @ResponseBody
    public byte[] createQRImage(String startPage) {
        String startAddr = "http://"+GameController.getIp()+":8080/"+startPage;
        QrCode qr0 = QrCode.encodeText(startAddr, QrCode.Ecc.MEDIUM);
        String imageSVG = qr0.toSvgString(1);
        //System.out.println(imageSVG);
        return imageSVG.getBytes();
    }

    @RequestMapping(value={"/connect/{deviceHash}"}, method=RequestMethod.GET)
    public String connectDevice(@PathVariable(value="deviceHash") String deviceHash) {
        String result = this.game.addDevice(deviceHash);
        if ("OK".equals(result)) {
            System.out.println("Added device: " + deviceHash);
        } else {
            System.out.println("Problem added device: "+deviceHash);
        }
        return result;
    }

    @PostMapping(value={"/addPlayer"})
    public ResponseEntity<String> addPlayer(@RequestBody AddPlayerForm addPlayerForm) {
        String result = this.game.addPlayer(addPlayerForm.getDeviceHash(), addPlayerForm.getPlayerName(), addPlayerForm.getColorName());
        if ("OK".equals(result)) {
            System.out.println("Added Player:" + addPlayerForm);
            return new ResponseEntity("OK", HttpStatus.OK);
        }
        System.out.println("Problem while adding Player:" + addPlayerForm);
        return new ResponseEntity(result, HttpStatus.CONFLICT);
    }

    @RequestMapping(value={"/getPlayer/{playerId}"}, method=RequestMethod.GET)
    public ResponseEntity<Player> getPlayer(@PathVariable(value="playerId") int playerId) {
        Player player = this.game.getPlayer(playerId);
        if (player == null) {
            return new ResponseEntity(player, HttpStatus.NOT_FOUND);
        }
        ResponseEntity response = new ResponseEntity(player, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/playerUp/{playerId}"}, method=RequestMethod.GET)
    public ResponseEntity<Player> playerUp(@PathVariable(value="playerId") int playerId) {
        Player player = this.game.getPlayer(playerId);
        if (player == null) {
            return new ResponseEntity(player, HttpStatus.NOT_FOUND);
        }
        this.game.playerUp(player);
        ResponseEntity response = new ResponseEntity(player, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/playerDown/{playerId}"}, method=RequestMethod.GET)
    public ResponseEntity<Player> playerDown(@PathVariable(value="playerId") int playerId) {
        Player player = this.game.getPlayer(playerId);
        if (player == null) {
            return new ResponseEntity(player, HttpStatus.NOT_FOUND);
        }
        this.game.playerDown(player);
        ResponseEntity response = new ResponseEntity(player, HttpStatus.OK);
        return response;
    }

/*    @RequestMapping(value={"/conflictreset/{playerId}"})
    public ResponseEntity<Integer> conflictReset(@PathVariable(value="playerId") int playerId) {
        Player player = this.game.getPlayer(playerId);
        if (player == null) {
            return new ResponseEntity(new Integer(-1), HttpStatus.NOT_FOUND);
        }
        int resetNum = this.game.conflictReset(player);
        ResponseEntity response = new ResponseEntity(resetNum, HttpStatus.OK);
        return response;
    }*/

    /*@RequestMapping(value={"/nextRound"})
    public ResponseEntity<Integer> nextRound() {
        int round = this.game.nextRound();
        ResponseEntity response = new ResponseEntity(round, HttpStatus.OK);
        return response;
    }*/

    @RequestMapping(value={"/getgame"}, method=RequestMethod.GET)
    public ResponseEntity<Game> getGame() {
        /*
        // for debuging:
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonInString = mapper.writeValueAsString(this.game);
            System.out.println(jsonInString +"\n");
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        ResponseEntity response = new ResponseEntity(this.game, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/getfield"}, method=RequestMethod.GET)
    public ResponseEntity<GameField> getField() {
        /*
        // for debuging:
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonInString = mapper.writeValueAsString(this.game);
            System.out.println(jsonInString +"\n");
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        ResponseEntity response = new ResponseEntity(this.game.getGameField(), HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/gettiles"}, method=RequestMethod.GET)
    public ResponseEntity<List<GameTileType>> getTiles() {
        /*
        // for debuging:
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonInString = mapper.writeValueAsString(this.game);
            System.out.println(jsonInString +"\n");
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        ResponseEntity response = new ResponseEntity(GameTileType.getAllTileTypes(), HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/getlog"}, method=RequestMethod.GET)
    public ResponseEntity<GameLog> getGameLog() {
        ResponseEntity response = new ResponseEntity(this.game.getGameLog(), HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/getgameversion"}, method=RequestMethod.GET)
    public ResponseEntity<Integer> getGameVersion() {
        Integer res = this.game.getStateVersion();
        ResponseEntity response = new ResponseEntity(res, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/startgame"}, method=RequestMethod.GET)
    public ResponseEntity<Integer> startGame() {
        Integer res = this.game.startGame();
        ResponseEntity response = new ResponseEntity(res, HttpStatus.OK);
        return response;
    }

    /*@PostMapping(value={"/setowncard"})
    public ResponseEntity<String> setOwnCard(@RequestBody SetCardForm setCardForm) {
        String result = this.game.setOwnCard(setCardForm.getPlayerId(), setCardForm.getOwnCard());
        if ("OK".equals(result)) {
            System.out.println("Player Own Choice:" + setCardForm);
            return new ResponseEntity("OK", HttpStatus.OK);
        }
        System.out.println("Problem with own choice:" + setCardForm);
        return new ResponseEntity(result, HttpStatus.CONFLICT);
    }

    @PostMapping(value={"/setcards"})
    public ResponseEntity<String> setCards(@RequestBody SetCardForm setCardForm) {
        String result = this.game.setChoiceCard(setCardForm.getPlayer(), setCardForm.getOwnCard(), setCardForm.getGuessCard());
        if ("OK".equals(result)) {
            System.out.println("Player Choice:" + setCardForm);
            return new ResponseEntity("OK", HttpStatus.OK);
        }
        System.out.println("Problem with choice:" + setCardForm);
        return new ResponseEntity(result, HttpStatus.CONFLICT);
    }*/

    public static void setTestMode(boolean b) {
        testMode = b;
    }
}

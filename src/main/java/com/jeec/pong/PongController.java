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
package com.jeec.pong;

import com.jeec.game.forms.AddPlayerForm;
import com.jeec.pong.form.CallForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

@RestController
@CrossOrigin(origins={"*"})
@RequestMapping("/pong")
public class PongController {
    private String ip;
    private PongGame pongGame = new PongGame();
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
    public ResponseEntity<String> testit() {
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @PostMapping(value={"/player"})
    public ResponseEntity<String> addPlayer(@RequestBody AddPlayerForm addPlayerForm) {

        String result = this.pongGame.addPlayer(addPlayerForm.getDeviceHash(), addPlayerForm.getPlayerName());
        if ("OK".equals(result)) {
            System.out.println("Added Player:" + addPlayerForm);
            return new ResponseEntity("OK", HttpStatus.OK);
        }
        System.out.println("Problem while adding Player:" + addPlayerForm);
        return new ResponseEntity(result, HttpStatus.CONFLICT);
    }

    @RequestMapping(value={"/version"}, method=RequestMethod.GET)
    public ResponseEntity<Integer> getGameVersion() {
        Integer res = this.pongGame.getVersion();
        ResponseEntity response = new ResponseEntity(res, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/timing"}, method=RequestMethod.GET)
    public ResponseEntity<Integer> getTiming() {
        Integer res = this.pongGame.getTiming();
        ResponseEntity response = new ResponseEntity(res, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/playerlist"}, method=RequestMethod.GET)
    public ResponseEntity<List<PongPlayer>> getPlayerList() {
        List<PongPlayer> res = this.pongGame.getPongPlayers();
        ResponseEntity response = new ResponseEntity(res, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/pos"}, method=RequestMethod.GET)
    public ResponseEntity<String> getPos() {
        String res = this.pongGame.getPos();
        ResponseEntity response = new ResponseEntity(res, HttpStatus.OK);
        return response;
    }

    @PostMapping(value={"/call"})
    public ResponseEntity<String> callPlayer(@RequestBody CallForm callForm) {

        String result = this.pongGame.callPlayer(callForm.getCaller(), callForm.getCalled());
        if ("OK".equals(result)) {
            System.out.println("Called to play:" + callForm);
            return new ResponseEntity("OK", HttpStatus.OK);
        }
        System.out.println("Problem while calling Player:" + callForm);
        return new ResponseEntity(result, HttpStatus.CONFLICT);
    }


    /*
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
    } ***

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
        }*
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
        }* *
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
        }* *
        ResponseEntity response = new ResponseEntity(GameTileType.getAllTileTypes(), HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/getlog"}, method=RequestMethod.GET)
    public ResponseEntity<GameLog> getGameLog() {
        ResponseEntity response = new ResponseEntity(this.game.getGameLog(), HttpStatus.OK);
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

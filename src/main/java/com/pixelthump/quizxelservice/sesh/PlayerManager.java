package com.pixelthump.quizxelservice.sesh;
import com.pixelthump.quizxelservice.sesh.model.Player;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class PlayerManager {

    static final Integer MAX_PLAYERS = 5;
    @Getter
    @Setter
    private boolean isJoinable;
    private final Map<String, Player> players;
    private String hostId;

    public PlayerManager() {

        this.isJoinable = false;
        this.players = new HashMap<>();
    }

    public boolean joinAsHost(String playerId) {

        if (hasHostJoined()) {

            return false;
        }
        this.hostId = playerId;
        this.isJoinable = true;
        return true;
    }

    public boolean joinAsPlayer(String playerName, String playerId) {

        if (hasPlayerAlreadyJoinedByName(playerName)) return false;
        if (hasPlayerAlreadyJoinedByPlayerId(playerId)) return false;

        Player player = new Player(playerName, playerId);
        this.players.put(playerId, player);
        this.isJoinable = !isSeshFull();

        return true;
    }

    private boolean hasPlayerAlreadyJoinedByName(String playerName) {

        boolean playerHasJoinedAlready = this.players.values().stream().anyMatch(player -> player.getPlayerName().equals(playerName));
        return playerHasJoinedAlready || (playerName.equals("Host") && (this.hostId != null));
    }

    public boolean hasPlayerAlreadyJoinedByPlayerId(String playerId) {

        return this.players.containsKey(playerId);
    }

    public boolean hasHostJoined() {

        return this.hostId != null;
    }

    public boolean isSeshFull() {

        return this.players.size() >= MAX_PLAYERS;
    }

    public List<Player> getPlayers() {

        return new ArrayList<>(this.players.values());
    }

    public boolean isVIP(String playerId) {

        return this.players.get(playerId).getVip();
    }

    public void setVIP(String playerId) {

        if (this.players.values().stream().anyMatch(Player::getVip)) return;
        if (!this.players.containsKey(playerId)) return;

        this.players.get(playerId).setVip(true);
    }

    public boolean hasVIP() {

        return this.players.values().stream().anyMatch(Player::getVip);
    }

    public Player getPlayer(String buzzedPlayerId) {

        return this.players.get(buzzedPlayerId);
    }

    public Long getPlayerCount() {

        return (long) this.players.size();
    }

    public String getHostId() {

        return this.hostId;
    }

    public String getVipId() {

        return this.players.values().stream().filter(Player::getVip).toList().get(0).getPlayerId();
    }
}

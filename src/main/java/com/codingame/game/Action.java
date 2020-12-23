package com.codingame.game;

public class Action {
    public final int col;
    public final String player_given_viewer_message;
    public Player player;

    public Action(Player player, int col, String player_given_viewer_message) {
        this.player = player;
        this.col = col;
        this.player_given_viewer_message = player_given_viewer_message;
    }

    @Override
    public String toString() {
        return "" + col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Action) {
            Action other = (Action) obj;
            return col == other.col;
        } else {
            return false;
        }
    }
}

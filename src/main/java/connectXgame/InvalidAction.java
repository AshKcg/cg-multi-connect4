package connectXgame;

public class InvalidAction extends Exception {
    private static final long serialVersionUID = -8185589153224401564L;

    public static final int ACTION_NOT_INTEGER_OR_OUT_OF_BOUNDS = 1;
    public static final int ACTION_FILLED_COLUMN = 2;
    public static final int ACTION_STEAL_NOT_ALLOWED_IN_THIS_TURN = 3;

    private String playerAction;
    private int actionType;  // will be one of the above constants  // tells, why the action is invalid

    public InvalidAction(String errorMessage, String playerAction, int invalid_action_type) {
        super(errorMessage);
        this.playerAction = playerAction;
        this.actionType = invalid_action_type;
    }

    // an overriding constructor that can be used with an integer action (i.e which is parsed into column index)
    public InvalidAction(String errorMessage, int playerAction, int invalid_action_type) {
        super(errorMessage);
        this.playerAction = "" + playerAction;
        this.actionType = invalid_action_type;
    }

    public String getPlayerAction() {
        return playerAction;
    }

    public int getActionType() {
        return actionType;
    }
}

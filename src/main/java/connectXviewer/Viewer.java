package connectXviewer;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;
import com.codingame.gameengine.module.entities.Line;

import connectXgame.Connect4Board;  // for constants in it
import connectXgame.RecentChipConnectionsDetails;
import connectXgame.Cell;


public class Viewer {

    private static final String SPRITE_BOARD_TILE = "board_tile_91_in_105.png";
    private static final String SPRITE_CHIP = "chip_91_in_105.png";
    private static final String SPRITE_DOWN_ARROW = "down_arrow_105.png";

    // the following constant integers are dimensions and details of the above chosen images
    // todo if the above images are changed, please update the constants below
    private static final int SPRITE_DETAIL_TILE_HEIGHT = 105;
    private static final int SPRITE_DETAIL_TILE_WIDTH = 105;  // tile is a repeating block of the board, it is a square with a circular hole for chip
    private static final int SPRITE_DETAIL_BOTTOM_Y_TO_REST_THE_BOARD = 1080 - 75;  // Note: board in y direction is rested on a chosen bottom point (like resting on floor)
    private static final int BOARD_BORDER_WIDTH = 10;  // a rectangular border of this width is drawn around the board

    private static final int BOARD_COLOR = 0x75694F;

    private static final int BOARD_LENGTH = SPRITE_DETAIL_TILE_WIDTH * Connect4Board.NUM_COLS + 2 * BOARD_BORDER_WIDTH;
    private static final int BOARD_HEIGHT = SPRITE_DETAIL_TILE_HEIGHT * Connect4Board.NUM_ROWS + 2 * BOARD_BORDER_WIDTH;

    private GraphicEntityModule graphicEntityModule;  // GraphicEntityModule instance is injected into Referee which then sets this variable to the injected instance
    private Sprite viewerDownArrowForCurMove;
    private Text graphicsTextTurnIndex;
    private Text graphicsTextPlayerViewerMessage[] = {null, null};

    public void setGraphicEntityModule(GraphicEntityModule graphicEntityModule) {
        this.graphicEntityModule = graphicEntityModule;
    }

    public void drawInitialEntities(
            String p0NameToken, String p0AvatarToken, int p0ColorToken,
            String p1NameToken, String p1AvatarToken, int p1ColorToken) {

        // set cg's background image (given with tic tac toe)
        graphicEntityModule.createSprite()
                .setX(0)
                .setY(0)
                .setImage("Background.jpg");

        // draw players' images and names

        final String playerNames[] = {p0NameToken, p1NameToken};
        final String playerAvatars[] = {p0AvatarToken, p1AvatarToken};
        final int playerColors[] = {p0ColorToken, p1ColorToken};

        for (int playerIndex = 0; playerIndex < 2; playerIndex++) {

            int x = playerIndex == 0 ? 250 : 1920 - 250;
            int y = 220;

            graphicEntityModule
                    .createRectangle()
                    .setWidth(140)
                    .setHeight(140)
                    .setX(x - 70)
                    .setY(y - 70)
                    .setLineWidth(0)
                    .setFillColor(playerColors[playerIndex]);

            graphicEntityModule
                    .createRectangle()
                    .setWidth(120)
                    .setHeight(120)
                    .setX(x - 60)
                    .setY(y - 60)
                    .setLineWidth(0)
                    .setFillColor(0xffffff);

            Text text = graphicEntityModule.createText(playerNames[playerIndex])
                    .setX(x)
                    .setY(y + 120)
                    .setZIndex(20)
                    .setFontSize(40)
                    .setFillColor(0xffffff)
                    .setAnchor(0.5);

            Sprite avatar = graphicEntityModule.createSprite()
                    .setX(x)
                    .setY(y)
                    .setZIndex(20)
                    .setImage(playerAvatars[playerIndex])
                    .setAnchor(0.5)
                    .setBaseHeight(116)
                    .setBaseWidth(116);

            graphicsTextPlayerViewerMessage[playerIndex] = graphicEntityModule.createText("")
                    .setX(x)
                    .setY(y + 150)
                    .setZIndex(20)
                    .setFontSize(35)
                    .setFillColor(0xffffff)
                    .setAnchorX(0.5)
                    .setAnchorY(0);
        }

        // draw board
        for (int r = 0; r < Connect4Board.NUM_ROWS; r++) {
            int y = getViewerYforChip(r);
            for (int c = 0; c < Connect4Board.NUM_COLS; c++) {
                int x = getViewerXforChip(c);
                graphicEntityModule.createSprite()
                        .setX(x)
                        .setY(y)
                        .setAnchor(0.5)
                        .setZIndex(19)
                        .setImage(SPRITE_BOARD_TILE)
                        .setTint(BOARD_COLOR);
            }
        }
        // draw border around the board
        graphicEntityModule.createRectangle()
                .setX(getViewerXforChip(0) - SPRITE_DETAIL_TILE_WIDTH / 2 - BOARD_BORDER_WIDTH / 2)
                .setY(getViewerYforChip(0) - SPRITE_DETAIL_TILE_HEIGHT / 2 - BOARD_BORDER_WIDTH / 2)
                .setHeight(BOARD_HEIGHT - BOARD_BORDER_WIDTH - 1)  // (-borderWidth) because it is somehow 1 time bigger than the border width while enclosing;
                // and if -1 wasn't there, sometimes a black line appears between board and its border on the right side
                .setWidth(BOARD_LENGTH - BOARD_BORDER_WIDTH - 1)  // same reasons as above
                .setFillAlpha(0)
                .setLineWidth(BOARD_BORDER_WIDTH)
                .setLineColor(BOARD_COLOR)
                .setZIndex(19);

        // draw texts: column numbers
        for (int c = 0; c < Connect4Board.NUM_COLS; c++) {
            graphicEntityModule.createText(Integer.toString(c))
                    .setX(getViewerXforChip(c))
                    .setY(getViewerYforChip(-1))
                    .setZIndex(20)
                    .setFontSize(40)
                    .setFillColor(0xffffff)
                    .setAnchor(0.5);
        }

        // draw turn index (constant text: label "Turn index")
        graphicsTextTurnIndex = graphicEntityModule.createText("Turn index: --")
                .setX(getViewerXforChip(Connect4Board.NUM_COLS/2))
                .setY(getViewerYforChip(-2))
                .setZIndex(20)
                .setFontFamily("monospace, monospace")  // single "monospace" may not work in some browsers (see reason in below website)
                // https://stackoverflow.com/questions/38781089/font-family-monospace-monospace
                .setFontSize(40)
                .setFontWeight(Text.FontWeight.BOLD)
                .setFillColor(0xffffff)
                .setAnchorX(0.5)
                .setAnchorY(0.5);

        viewerDownArrowForCurMove = graphicEntityModule.createSprite()
                .setAnchorX(0.5)
                .setAnchorY(0.5)
                .setImage(SPRITE_DOWN_ARROW)
                .setX(-500)
                .setY(-500);
    }

    public void hideTheDownArrowForCurMove() {
        viewerDownArrowForCurMove
                .setX(-500)
                .setY(-500);
        graphicEntityModule.commitEntityState(0, viewerDownArrowForCurMove);
    }

    private double curTimeInAnimationState = 0;  // this goes from 0 to 1 (useful for putting win-lines, gameResultString right after chip's animation
    // reset to 0 on each update turn index because it is what is called at the beginning of each turn

    public void updateTurnIndex(int turnIndex) {
        graphicsTextTurnIndex.setText(String.format("Turn index: %02d", turnIndex));
        graphicEntityModule.commitEntityState(0, graphicsTextTurnIndex);

        curTimeInAnimationState = 0;
    }

    public void showPlayerViewerMessage(int curPlayerIndex, String playerMessage) {
        // show player's viewer message, and other player's text must disappear
        graphicsTextPlayerViewerMessage[curPlayerIndex].setText(playerMessage);
        graphicEntityModule.commitEntityState(0, graphicsTextPlayerViewerMessage[curPlayerIndex]);
    }

    public void putPlayerChipAndAnimateIt(int playerColorToken, int settlingRow, int col) {
        // put player's chip
        Sprite chip = graphicEntityModule.createSprite()
                .setImage(SPRITE_CHIP)
                .setX(getViewerXforChip(col))
                .setAnchorX(0.5)
                .setY(getViewerYforChip(-1))
                .setAnchorY(0.5)
                .setTint(playerColorToken);

        viewerDownArrowForCurMove
                .setX(chip.getX())
                .setY(chip.getY())
                .setTint(playerColorToken);
        graphicEntityModule.commitEntityState(0, viewerDownArrowForCurMove);  // commit at 0, so that it snaps to the new position from previous position

        graphicEntityModule.commitEntityState(0, chip);
        graphicEntityModule.commitEntityState(0.1, chip);

        // animate chip movement
        for (int i = 2, curRow = 0; i < 10; i++, curRow++) {  // from 0.1 secs, go to row=0 to settling row for every 0.1 increment
            if (curRow > settlingRow) break;
            chip.setY(getViewerYforChip(curRow));
            curTimeInAnimationState = 0.025 * i;
            graphicEntityModule.commitEntityState(curTimeInAnimationState, chip);  // as first will be 0.2, it starts travelling from 0.1
        }
    }

    public void drawWinConnectionLines(RecentChipConnectionsDetails chipConnectionsDetails) {
        for (int directionIndex = 0; directionIndex < RecentChipConnectionsDetails.DIRECTIONS_FOR_CHECKING_CONNECTIONS.length; directionIndex++) {
            if (!chipConnectionsDetails.hasConnectedRequiredNumberOfChips(directionIndex)) continue;
            Cell endPoint1 = chipConnectionsDetails.getRecentCell().getNeighbor(
                    RecentChipConnectionsDetails.DIRECTIONS_FOR_CHECKING_CONNECTIONS[directionIndex][0],
                    chipConnectionsDetails.getNumConnectedCells(directionIndex, 0));
            Cell endPoint2 = chipConnectionsDetails.getRecentCell().getNeighbor(
                    RecentChipConnectionsDetails.DIRECTIONS_FOR_CHECKING_CONNECTIONS[directionIndex][1],
                    chipConnectionsDetails.getNumConnectedCells(directionIndex, 1));
            Line line = graphicEntityModule.createLine()
                    .setX(getViewerXforChip(endPoint1.getCol()))
                    .setY(getViewerYforChip(endPoint1.getRow()))
                    .setX2(getViewerXforChip(endPoint2.getCol()))
                    .setY2(getViewerYforChip(endPoint2.getRow()))
                    .setLineColor(0xf9b700)
                    .setLineWidth(10)
                    .setZIndex(50);
            graphicEntityModule.commitEntityState(curTimeInAnimationState, line);
        }
    }

    public void setGameResultString(String gameResultString, int gameResultStringColor) {
        Text text = graphicEntityModule.createText(gameResultString)
                .setX(getViewerXforChip(Connect4Board.NUM_COLS/2))
                .setY((1080 + getBoardBottomY())/2)
                .setZIndex(20)
                .setFontSize(40)
                .setFillColor(gameResultStringColor)
                .setAnchor(0.5);
        graphicEntityModule.commitEntityState(curTimeInAnimationState, text);
    }

    private static int getViewerXforChip(int col) {
        return (1920 - BOARD_LENGTH) / 2  // start of board in x direction
                + BOARD_BORDER_WIDTH  // pass the border
                + SPRITE_DETAIL_TILE_WIDTH * col  // pass number of tiles given by col
                + SPRITE_DETAIL_TILE_WIDTH / 2;  // centre of current col
    }

    private static int getViewerYforChip(int row) {
        return (SPRITE_DETAIL_BOTTOM_Y_TO_REST_THE_BOARD - BOARD_HEIGHT)  // start of board in y direction.
                + BOARD_BORDER_WIDTH  // pass the border
                + SPRITE_DETAIL_TILE_HEIGHT * row  // pass number of tiles given by row
                + SPRITE_DETAIL_TILE_HEIGHT / 2;  // centre of current row
    }

    private static int getBoardBottomY() {
        return SPRITE_DETAIL_BOTTOM_Y_TO_REST_THE_BOARD;
    }

    public void placeAndAnimateSteal(int playerColorToken, int stolenRow, int stolenCol) {
        viewerDownArrowForCurMove
                .setX(getViewerXforChip(stolenCol))
                .setY(getViewerYforChip(-1))
                .setTint(playerColorToken);
        graphicEntityModule.commitEntityState(0, viewerDownArrowForCurMove);  // commit at 0, so that it snaps to the new position from previous position

        // put player's chip
        Sprite chip = graphicEntityModule.createSprite()
                .setImage(SPRITE_CHIP)
                .setX(getViewerXforChip(stolenCol))
                .setAnchorX(0.5)
                .setY(getViewerYforChip(stolenRow))
                .setAnchorY(0.5)
                .setTint(playerColorToken)
                .setScale(0);

        double fractions_of_sizes[] = {0, 1};

        for (int i = 0; i < fractions_of_sizes.length; i++) {
            double chip_size_fraction = fractions_of_sizes[i];
            double commit_time = i * 0.4;
            chip.setScale(chip_size_fraction);
            graphicEntityModule.commitEntityState(commit_time, chip);
        }

        // the following fixes the bug of incomplete overlying chip when pressing next move button in viewer
        // what it does is, it puts a new overlying chip at the end of Steal turn
        Sprite overlyingChip = graphicEntityModule.createSprite()
                .setImage(SPRITE_CHIP)
                .setX(getViewerXforChip(stolenCol))
                .setAnchorX(0.5)
                .setY(getViewerYforChip(stolenRow))
                .setAnchorY(0.5)
                .setTint(playerColorToken);
        graphicEntityModule.commitEntityState(1, overlyingChip);
    }
}

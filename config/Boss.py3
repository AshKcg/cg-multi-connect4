import sys
import math
from random import randint


# Drop chips in the columns.
# Connect at least 4 of your chips in any direction to win.

# my_id: 0 or 1 (Player 0 plays first)
# opp_id: if your index is 0, this will be 1, and vice versa
my_id, opp_id = [int(i) for i in input().split()]

# game loop
while True:
    turn_index = int(input())  # starts from 0; As the game progresses, Player0 gets [0,2,4,...,40] and Player1 gets [1,3,5,...,41]
    for i in range(7):
        board_row = input()  # one row of the board (from top to bottom)
    num_valid_actions = int(input())  # number of unfilled columns in the board
    available_actions = []
    for i in range(num_valid_actions):
        action = int(input())  # a valid column index into which a chip can be dropped
        available_actions.append(action)
    opp_previous_action = int(input())  # opponent's previous chosen column index (will be -1 for Player 0 in the first turn)

    # Write an answer using print
    # To debug: print("Debug messages...", file=sys.stderr)


    # Output a column index to drop the chip in. Append message to show in the viewer.
    print(available_actions[randint(0, len(available_actions)-1)])

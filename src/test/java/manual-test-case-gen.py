"""
USAGE:
Left click on buttons to put colors of players.
Red, Blue for first and second players are automatically placed.
To undo, press any key. (no option for redo)
After the GUI is closed, it prints, two arrays:
    The second array is column indices, usable in BotFixedGamePlay.java
    The first array which is an array of (row, col) tuples can be used with go_forward function in this script,
    so that, when the script is run again, we can start from there.
"""


import Tkinter as Tk

ROWS = 7
COLS = 9


button_click_order = []
buttons = [[] for _ in range(ROWS)]


def button_clicked(event):
    global button_click_order
    if len(button_click_order) % 2 == 0:  # first player plays
        color = "Red"
    else:
        color = "Blue"
    event.widget.configure(bg=color)
    button_click_order.append((event.widget.rowIndex, event.widget.columnIndex))
    # print button_click_order

def go_back(event):
    global button_click_order
    global buttons
    try:
        row, col = button_click_order.pop()
    except IndexError:
        print "Board is empty"
        return
    buttons[row][col].configure(bg="white")

def go_forward(newList):
    global button_click_order
    button_click_order = []
    for row, col in newList:
        if len(button_click_order) % 2 == 0:  # first player plays
            color = "Red"
        else:
            color = "Blue"
        buttons[row][col].configure(bg=color)
        button_click_order.append((row, col))

root = Tk.Tk()
for r in range(ROWS):
    for c in range(COLS):
        b = Tk.Button(root, width=3, bg="white")
        b.grid(row=r, column=c)
        b.columnIndex = c
        b.rowIndex = r
        buttons[r].append(b)
        b.bind("<Button-1>", button_clicked)
root.bind_all("<Key>", go_back)

go_forward([(6, 0), (6, 1), (6, 2), (6, 3), (6, 4), (6, 5), (6, 6), (6, 7), (6, 8), (5, 0), (5, 1), (5, 2), (4, 0), (5, 8), (5, 7), (5, 6), (4, 8), (5, 3), (5, 4), (5, 5), (4, 7), (4, 4), (4, 1), (3, 8), (4, 5), (3, 0), (4, 3), (4, 2), (2, 8), (4, 6), (2, 0), (3, 1), (3, 2), (3, 7), (3, 6), (3, 4), (3, 3), (1, 0), (3, 5), (1, 8), (2, 4), (2, 1), (1, 4), (2, 7), (1, 7), (2, 6), (1, 1), (2, 2), (0, 4), (2, 3), (0, 7), (2, 5), (0, 8), (1, 5), (0, 0), (1, 3)])
root.mainloop()

print button_click_order
print list(map(lambda x: x[-1], button_click_order))

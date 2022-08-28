def h():
    print("hh")
    but2=tk.Button(win,command=g)
    but2.pack()
def g():
    print("aa")

import tkinter as tk

win=tk.Tk()
but=tk.Button(win,command=h)
but.pack()
win.mainloop()
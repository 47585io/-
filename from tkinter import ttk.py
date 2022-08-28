from tkinter import ttk
import tkinter as tk

root = tk.Tk()

listbox = tk.Listbox(root)
for i in range(20):
    listbox.insert('end', 'item %i' % i)

style = ttk.Style(root)
# create new scrollbar layout
style.layout('arrowless.Vertical.TScrollbar',
             [('Vertical.Scrollbar.trough',
               {'children': [('Vertical.Scrollbar.thumb',
                              {'expand': '1', 'sticky': 'nswe'})],
                'sticky': 'ns'})])
# create scrollbar without arrows
scroll = ttk.Scrollbar(root, orient='vertical', command=listbox.yview,
                       style='arrowless.Vertical.TScrollbar')
listbox.configure(yscrollcommand=scroll.set)

listbox.pack(side='left', fill='both', expand=True)
scroll.pack(side='right', fill='y')
root.mainloop()

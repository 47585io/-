class Graphics:
             '''you can use it, also can not use'''

             def set_input(self, size=(0, 0)):
             self.entfarme=tk.Frame(self.bgfarme, background=self.Color["bg"])
             self.xscro=tk.Scrollbar(self.entfarme, orient=tk.HORIZONTAL)
             self.ent=tk.Entry(self.entfarme, xscrollcommand=self.xscro.set)
             self.xscro.config(command=self.ent.xview)
             # after set, please pack them, self.entfarme.place()   self.ent.pack()   self.xscro.pack(fill=tk.X)

             def set_win(self, win):
             '''quick config a win'''
             

             def set_friends(self, friends):
             '''show friend_list'''
             while len(self.friend_list_but) < len(friends):
             self.friend_list_but.append(tk.Button())
             i=0
             while i < len(self.friend_list_but):
             self.friend_list_but[i].config(
                 text=friends[i], bg=str(random.randint))

             def Canvas_mess(Canvas):
             Canvas.g()

             def talk_lab():
             pass

             def Canvas_config(self,):
             '''quick config a have scro Canvas'''
             self.can_yscro=tk.Scrollbar(self.bgfarme,)
             self.can=tk.Canvas(
                 self.bgfarme, yscrollcommand=self.can_yscro.set)
             self.can_yscro.config(command=self.can.yview)

             def welcome(self):
             
             def welcom2(self):
             self.bgfarme.destroy()
             self.func.append(self.welcome,)
             self.index += 1
             self.but[1].pack()
             
             def return_but(self, win):
             self.index -= 1
             if self.index < 0:
             exit(0)
             fun=self.func[self.index]
             self.bgfarme.pack()
             fun()

             def init(self, win) -> None:
             '''set you config'''
             # On the this Label going to use many count, but their olny sava once
             # And below, that lab have many, but their going to while use
             
             self.but=[tk.Button(self.bgfarme), tk.Button(text='return', command=lambda:self.return_but(win), foreground=self.Color["fg"], font=(
                 self.Font["zheng"], self.Font_size["mid"], ), background=self.Color["bg"])]
             self.lbtmp=[]
             self.lab_maxsize=3
             self.lab_config()
             # the hava three diffrent page,For buf, the init defalut init first page, when user use other, loaded separately
             # why i am not use __init__? because the lab must after init window

             def geosize(self, tup=None):
             if tup:
             return str(tup[0][0])+"x"+str(tup[0][1])+"+"+str(tup[0][2])+"+"+str(tup[0][3])
             return str(self.Win_Size[0][0])+"x"+str(self.Win_Size[0][1])+"+"+str(self.Win_Size[0][2])+"+"+str(self.Win_Size[0][3])

             def quickconfig(self, win):
             '''you can call it use defalut config'''
             self.init(win)
             self.welcome()
             self.First_Page()
             self.Second_Page()
             self.Third_Page()
             # self.bgfarme.destroy()
             def First_Page(self,):

             pass

             def Second_Page(self):
             pass

             def Third_Page(self):
             pass
             )

import os
import random
import socket
import threading as th
import atexit
from time import sleep
import tkinter as tk
import tkinter.filedialog as fid
from tkinter import messagebox
from PIL import Image
import greenlet

Mess_Buffer = 1024
Max_Mess = 10
# recv buffer size and user mess_list size
UDP_SOCK = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
UDP_SOCK.bind(("", 0))
TCP_SOCK = socket.socket()
TCP_SOCK.bind(("", 0))
# the user, olny two port with server
USER_NAME = ""
Is_Loding=1
#...
MAX_THD=1
#the user can use every work max thread

def whenexit():
    '''when exit, close all sock'''
    UDP_SOCK.close()
    TCP_SOCK.close()


atexit.register(whenexit)


class List(list):
    def __init__(self):
        pass
# i want to, but i am busy now


class UDP_Mess:
    def __init__(self) -> None:
        self.MessCache = []
        self.index = -1
        self.yes = 0
# Mess list: Max 10 mess,  index: now new mess index, yes:the mess yes or on new mess

    def get(self):
        '''the func pop a element from MessCache head, and return'''
        if self.index >= 0:
            mess = self.MessCache[0]
            del self.MessCache[0]
            self.index -= 1
            return mess

    def getnew(self):
        '''get best new mess'''
        while self.yes == 0:
            pass
        self.yes = 0
        tmp = self.MessCache[self.index]
        del self.MessCache[self.index]
        self.index -= 1
        return tmp

    def Send(self, sock, send_str, to_user=None):
        '''when want send mess to server, call it'''
        self.yes = 0
        # when send a mess, Read going to read a new mess, the yes=0, now index is no new
        send_str = Spilt_Mess.Send_spilt(send_str, to_user)
        sock.sendto(send_str, ("127.0.0.1", 1234))

    def Read(self, sock):
        '''every once, recv a mess and add it to MessCache, if mess count >Max, del old mess'''
        while 1:
            tmp = sock.recvfrom(1024)
            self.MessCache.append(tmp[0])
            self.index += 1
            self.yes = 1
            if(self.index >= Max_Mess):
                del self.MessCache[0]
                self.index -= 1
            print(tmp[0])


UDP = UDP_Mess()
# mess object


class friends:
    '''The all friend save in'''

    def __init__(self) -> None:
        self.friend_list = ["qe","he"]
        # friend name list
        self.Mess_Friend = {}
        # 记录与好友通信mess label
        self.Talk_With = []
        # who talk with now

    def show(self):
        return self.friend_list

    def format_list(self, list_):
        '''format the going to add list'''
        new_list = []
        for name in list_:
            if name not in self.friend_list and name != USER_NAME:
                new_list.append(name)
        return new_list

    def __from_server_get_friend_list(self, mess, sock):
        '''func name is i mean'''
        mess.Send(sock, "AddFriend ")
        s_str = mess.getnew()
        return Spilt_Mess.Friend_list_Read_Spilt(s_str)

    def addfriend(self, mess, sock):
        '''get friend list and show'''
        list_ = self.__from_server_get_friend_list(mess, sock)
        list_ = self.format_list(list_)
        self.friend_list.extend(list_)

    def Search_Friend(self, name_str):
        '''search name_str in friend_list'''
        name_list = []
        i = 0
        while i < len(self.friend_list):
            if self.friend_list[i].find(name_str) != -1:
                name_list.append(i)


Friend_List = friends()
# sava all friend


class Spilt_Mess:
    @staticmethod
    def Send_spilt(s_str, name):
        if name:
            return (name+"@"+s_str).encode()
        else:
            return s_str.encode()

    @staticmethod
    def Read_spilt(s_str):
        if s_str:
            s_str = s_str.decode()
            index = s_str.find(('@'))
            read_str = s_str[index+1::]
            name = s_str[0:index:]
            return (read_str, name)

    @staticmethod
    def Friend_list_Send_Spilt(friend_list):
        s_str = ""
        for friend in friend_list:
            s_str += "'"
            s_str += friend
            s_str += "'"
        return s_str.encode()

    @staticmethod
    def Friend_list_Read_Spilt(s_str):
        s_str = s_str.decode()
        friend_list = []
        start, end, index = (0, 0, 0)
        while index < len(s_str):
            if s_str[index] == "'":
                start = index+1
                index += 1
                while True:
                    if s_str[index] == "'":
                        end = index
                        friend_list.append(s_str[start:end:])
                        break
                    index += 1
            index += 1
        return friend_list

    @staticmethod
    def Label_Add(s_str, num):
        return (str(num)+':'+s_str).encode()

    @staticmethod
    def Label_Del(s_str):
        s_str = s_str.decode()
        index = s_str.find(':')
        return (int(s_str[0:index:]), s_str[index+1::])

class Welcome:
    LAB_Count=3
    BUT_Count=3
    #lab and but init count
    def __init__(self) -> None:
        self.func=[]
        self.index=0
        self.Win_Size=[(360, 450, 1600, 1000)]
        self.Color={"bg": "#282c34", "fg": "#abb2bf","entblock":"#808080","ffg":"#484848" }
        self.Font={"zheng": "DejaVu Sans", "alpha": "Quicksand", 
                   "drak": "Quicksand Medium","small": "Z003", 
                   "beutful": "DejaVu Math TeX Gyre", "frmory": "Dingbats"}
        self.Font_size={"small": 5, "mid": 10, "big": 20}
        self.pic_size=[100,90]
        self.filename=""
        self.cache1=greenlet.greenlet(self.new)
    def init(self,):
        self.win=tk.Tk()
        self.bgfarme = tk.Frame(self.win)
        self.lab_list=[]
        self.but_list=[]
        self.message=tk.Message(self.bgfarme)
        #the index 1 is myself but,please no search
        for i in range(self.LAB_Count):
            self.lab_list.append(tk.Label(self.bgfarme))
        for i in range(self.BUT_Count):
            self.but_list.append(tk.Button(self.bgfarme))
    def init_ent(self):
        '''quick init a ent'''
        self.entfarme=tk.Frame(self.bgfarme)
        self.ent_scro=tk.Scrollbar(self.entfarme)
        self.ent=tk.Entry(self.entfarme,)
#on, please wirte all init fun on
        
    def geosize(self, tup=None):
        '''win size to str'''
        if tup:
            return str(tup[0][0])+"x"+str(tup[0][1])+"+"+str(tup[0][2])+"+"+str(tup[0][3])
        return str(self.Win_Size[0][0])+"x"+str(self.Win_Size[0][1])+"+"+str(self.Win_Size[0][2])+"+"+str(self.Win_Size[0][3])
    def clear(self):
        '''forget all lab on the bgfarme '''
        for wed in self.bgfarme.winfo_children():
            wed.pack_forget()            

    def go(self, go_fun, src_fun=None, mid_fun=None):
        '''use the fun go a new func, and save src fun, if you want to do other things,please give me the fun'''
        if mid_fun:
            mid_fun()
        self.clear()
        self.but_list[1].pack(anchor="nw")
        if src_fun:
            self.index+=1
            self.func.append(src_fun)
        go_fun()
    def retu(self,):
        '''pop and call a fun from func'''
        if self.index<1:
            exit(0)
        self.clear()
        self.but_list[1].pack(anchor="nw")
        self.index-=1
        fun=self.func[self.index]
        fun()
        print("finsh!")
        del self.func[self.index]
    
    def winconfig(self):
        self.win.config(bg=self.Color["bg"],)
        self.win.title("Sock QQ")
        self.win.geometry(self.geosize())
        #self.win.resizable(0, 0)
        self.bgfarme.config(background = self.Color["bg"], width = self.Win_Size[0][0], height = self.Win_Size[0][1])
        self.bgfarme.pack()
             # olny set once
    def labconfig(self,lab_list):
        for lab in lab_list:
            lab.config(anchor="nw", font=(self.Font["zheng"], self.Font_size["mid"]),
                                  borderwidth=0,foreground=self.Color["fg"], background=self.Color["bg"], width=int(self.Win_Size[0][0]/self.Font_size["mid"]))
    def butconfig(self,but_list):
        for but in but_list:
            but.config(font=(self.Font["zheng"], self.Font_size["mid"]),
                       borderwidth=0,highlightthickness=0, activebackground=self.Color["ffg"],  foreground=self.Color["fg"],activeforeground=self.Color["fg"], background=self.Color["bg"])
    def ent_config(self):
        self.entfarme.config(background=self.Color['bg'],)
        self.ent_scro.config(command=self.ent.xview, background=self.Color['fg'],
                             activebackground=self.Color["entblock"], borderwidth=0, orient=tk.HORIZONTAL, elementborderwidth=0, activerelief="sunken")
        self.ent.config(xscrollcommand=self.ent_scro.set, borderwidth=1, highlightbackground=self.Color['fg'],
                        highlightcolor=self.Color['fg'],
                        highlightthickness=1, insertbackground='#61afef',
                        bd=0, background = self.Color['bg'], fg = self.Color['fg'],)
        self.ent.pack(side='top')
        self.ent_scro.pack(side="bottom",fill=tk.X)
#please wirte all config fun on
    def quickconfig(self):
        '''usally, user olny call it, can init and config all lab'''
        self.init()
        self.winconfig()
        self.labconfig(self.lab_list)
        self.butconfig(self.but_list) 
        self.init_ent()
        self.ent_config()
        self.but_list[1].config(text="←", command=lambda: self.retu())
        
    def run(self):
        '''when config all lab , call it'''
        global USER_NAME
        tmp = self.openfile()
        if self.openfile()==0:
            self.go(self.welcome1)
        else:
            USER_NAME=tmp[0]    
            self.filename=tmp[1]      
            self.go(self.Login)
        self.win.mainloop()
        self.new()
        self.win.mainloop()
   
    def welcome1(self):
        self.lab_list[0].config(text="\nWelcome!", font=(self.Font["zheng"],20,"bold"))
        self.lab_list[0].pack()
        self.lab_list[1].config(text="\n\n一切刚刚好,现在立刻!\n\n",)
        self.lab_list[1].pack()
        self.but_list[0].config(text="Get Started",command=lambda :self.go(self.welcome2,self.welcome1))
        self.but_list[0].pack(side='right')
        print("call!")
    def welcome2(self):
        print("&")
        self.lab_list[0].config(text='\nSet Name',)
        self.lab_list[0].pack()
        self.lab_list[1].config(text="\n伟大的名字\n ")
        self.lab_list[1].pack()
        self.entfarme.pack()
        self.lab_list[2].config(text="\n")
        self.lab_list[2].pack()
        self.but_list[0].config(text="Enter", command=lambda: self.go(self.welcome3,self.welcome2,self.setname))
        self.but_list[0].pack(side='right') 
        self.ent.bind("<Return>", lambda k,x=self.welcome3, y=self.welcome2, z=self.setname: self.go(x,y,z))
    def welcome3(self):
        self.ent.unbind("<Return>")
        self.lab_list[0].config(text="\nChoose Avatar\n")
        self.lab_list[0].pack()
        self.lab_list[1].config(text="漂亮的头像\n")
        self.lab_list[1].pack()
        self.message.config(anchor="nw", font=(self.Font["zheng"], self.Font_size["mid"]),
                                foreground=self.Color["fg"], background=self.Color["bg"], width=self.Win_Size[0][0]-20)
        self.message.pack()
        self.but_list[0].config(command=self.choose,
                                text='Choose', borderwidth=0,)
        self.but_list[0].pack(side='left',)
        self.but_list[2].config(command=lambda :self.go(self.Login,self.welcome3,self.save),text="Login")
        self.but_list[2].pack(side='right')
    #on, three welcome page is really, after, you must set a login func
    
    def choose(self,):
        self.filename=fid.askopenfilename(initialdir="../",filetypes=[("PNG","*.png"), ("JPG","*.jpg"),("GIF","*.gif"),])
        if self.filename!=():
            self.message.config(text=self.filename+"\n")
    def save(self):
        if self.filename==():
            return
        if self.filename.find(".jpg"):
            newfile=Image.open(self.filename,"r")
            newfile.thumbnail((300,700))
            newfile.convert("RGBA")
            mk=os.path.abspath("./")
            newfile.save(mk+"/new.png")
            self.filename=mk+"/new.png"
            print("save in ", mk+"/new.png")
        file=open("name.txt","w")
        file.writelines([USER_NAME+"\n",self.filename])
        file.close()
    def setname(self):
        global USER_NAME
        USER_NAME=self.ent.get()
        print("hello",USER_NAME)
    def openfile(self):
        file=open("name.txt","r+")
        tmp=file.readlines()
        if tmp==[]:
            return 0
        else:
            return tmp

    def Login(self): 
        global USER_NAME       
        self.lab_list[0].config(text='\nHello!\n',anchor='center',font=(self.Font["zheng"],self.Font_size["big"],"bold"))
        self.lab_list[0].pack()
        self.message.config(text=USER_NAME, anchor="nw", font=(self.Font["zheng"], self.Font_size["mid"]),
                            foreground=self.Color["fg"], background=self.Color["bg"], width=self.Win_Size[0][0]-20)
        
        self.furry=tk.PhotoImage(file=self.filename)
        self.lab_list[1].config(width=self.pic_size[0],height=self.pic_size[1],image=self.furry)
        self.lab_list[1].pack()
        self.lab_list[2].pack()
        self.message.pack()
        self.win.update()
        sleep(2)
        self.cache1.switch()
    def new(self): 
        '''this is Extended access''' 
        pass

#the class can't to new class!!!!
#or, try lab?
#no! it not have scro!
#but we must 继承 baseclass, 否则 all lab going to del
#why?.... why not?

class Friend_list(Welcome):
    def __init__(self) -> None:
        Welcome.__init__(self)
        self.Canv_x=self.Font_size["mid"]
        self.Canv_y_from=20
        self.Canv_size=(self.Win_Size[0][0],self.pic_size[1])
        self.furry=[]
    def init(self,):
        Welcome.init(self)
        self.canv_init()
    def canv_init(self,):
        self.f_can = tk.Canvas(self.bgfarme,highlightthickness=0, background=self.Color['bg'],borderwidth=0,)
        self.f_scro = tk.Scrollbar(self.bgfarme)
    def canvconfig(self,canv,scro):
        canv.config(width=self.Win_Size[0][0], height=self.Win_Size[0]
                    [1], borderwidth=0, yscrollcommand=scro.set,)
        scro.config(command=canv.yview,background=self.Color['fg'],
                             activebackground=self.Color["entblock"],borderwidth=0,elementborderwidth=0,activerelief="sunken")     
    def quickconfig(self,friends):
        Welcome.quickconfig(self)
        self.canvconfig(self.f_can,self.f_scro)
        self.fren = friends
    def new(self):       
        self.go(self.showfriends)
        self.but_list[2].config(command=self.searchfriend)       
        pass
    def place_forgets(*arg):
        '''it going to place_forget all arg as mid fun before pack_forget'''
        for a in arg:
            a.place_forgets()
        pass
    
    def draw_a_friend(self,canv,name):
        canv.create_rectangle(
            0, self.Canv_x, self.Canv_size[0], self.Canv_x+self.Canv_size[1], activefill=self.Color['ffg'], outline=self.Color['bg'])
        canv.create_text(20, self.Canv_x,text=name,fill=self.Color['fg'],)
        self.Canv_x += self.Canv_size[1]
        pass
    def showfriends(self):
        self.but_list[0].config(text="+",command=self.addfriend)
        self.but_list[0].place(x=self.Win_Size[0][0]-40,y=0)
        #self.lab_list[1].pack()
        self.f_scro.pack(fill=tk.Y, side='right')
        self.f_can.pack()
    #but_list[0]
        for f in self.fren.friend_list:
            self.draw_a_friend(self.f_can,f)
        #print("he")
    def addfriend(self):
        
        pass
    def searchfriend(self):
        pass

       
class Graphics(Friend_list):
    def __init__(self) -> None:
        Friend_list.__init__(self)
        pass
    
    
class Talk_with:
    pass

GNU = Friend_list()


def main(mess, sock, friends, gra):
    mess.Send(sock, "LOGIN "+USER_NAME)
    for i in range(MAX_THD):
        r = th.Thread(target=mess.Read, args=(sock,))
        r.setDaemon(True)
        r.start()
    gra.quickconfig(friends)
    gra.run()


main(UDP, UDP_SOCK, Friend_List, GNU)


'''
mess.Send(sock, "LOGIN H")
    friends.addfriend(mess,sock)
    while True:
        s=input("please input: ")
        mess.Send(sock,s)'''

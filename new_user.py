import random
from re import X
import socket
import threading as th
import atexit
from time import sleep
import tkinter as tk
import tkinter.filedialog as fid
from tkinter import messagebox

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
        self.friend_list = []
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
    LAB_Count=2
    BUT_Count=2
    win=tk.Tk()
    def __init__(self) -> None:
        self.func=[]
        self.index=0
        self.Win_Size=[(360, 450, 1600, 1000)]
        self.Color={"bg": "#282c34", "fg": "#abb2bf", }
        self.Font={"zheng": "DejaVu Sans", "alpha": "Quicksand", 
                   "drak": "Quicksand Medium","small": "Z003", 
                   "beutful": "DejaVu Math TeX Gyre", "frmory": "Dingbats"}
        self.Font_size={"small": 5, "mid": 10, "big": 20}
    def init(self,):
        self.bgfarme = tk.Frame(self.win)
        self.lab_list=[]
        self.but_list=[]
        for i in range(self.LAB_Count):
            self.lab_list.append(tk.Label(self.bgfarme))
        for i in range(self.BUT_Count):
            self.but_list.append(tk.Button(self.bgfarme))
    def geosize(self, tup=None):
        if tup:
            return str(tup[0][0])+"x"+str(tup[0][1])+"+"+str(tup[0][2])+"+"+str(tup[0][3])
        return str(self.Win_Size[0][0])+"x"+str(self.Win_Size[0][1])+"+"+str(self.Win_Size[0][2])+"+"+str(self.Win_Size[0][3])
    def clear(self):
        for wed in self.bgfarme.winfo_children():
            wed.pack_forget()
            
        '''self.bgfarme.destroy()
        del self.bgfarme
        del self.lab_list
        del self.but_list
        self.init()
        self.quickconfig()'''
    def go(self,go_fun,src_fun):
        self.clear()
        self.index+=1
        self.func.append(src_fun)
        go_fun()
    def retu(self,):
        if self.index<1:
            return
        self.clear()
        self.index-=1
        fun=self.func[self.index]
        fun()
        print("finsh!")
        del self.func[self.index]
      
    def winconfig(self):
        self.win.config(bg=self.Color["bg"],)
        self.win.title("Sock QQ")
        self.win.geometry(self.geosize())
        self.win.resizable(0, 0)
        self.bgfarme.config(background = self.Color["bg"], width = self.Win_Size[0][0], height = self.Win_Size[0][1])
        self.bgfarme.pack()
             # olny set once
    def labconfig(self):
        for lab in self.lab_list:
            lab.config(anchor="nw", font=(self.Font["zheng"], self.Font_size["mid"]),
                                  foreground=self.Color["fg"], background=self.Color["bg"], width=int(self.Win_Size[0][0]/self.Font_size["mid"]))
    def butconfig(self):
        for but in self.but_list:
            but.config(font=(self.Font["zheng"], self.Font_size["mid"]),
                       activebackground=self.Color["fg"], activeforeground=self.Color["bg"], foreground=self.Color["fg"], background=self.Color["bg"])
    def quickconfig(self):
        self.init()
        self.winconfig()
        self.labconfig()
        self.butconfig()
        
    def run(self):
        self.welcome1()
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
        self.but_list[1].config(text="return",command=self.retu())
        self.but_list[1].pack()
        #self.retu()
       
class Graphics(Welcome):
    def __init__(self) -> None:
        super().__init__()

GNU = Graphics()


def main(mess, sock, friends, gra):
    mess.Send(sock, "LOGIN "+USER_NAME)
    for i in range(MAX_THD):
        r = th.Thread(target=mess.Read, args=(sock,))
        r.setDaemon(True)
        r.start()
    sleep(1)
    gra.quickconfig()
    gra.run()


main(UDP, UDP_SOCK, Friend_List, GNU)


'''
mess.Send(sock, "LOGIN H")
    friends.addfriend(mess,sock)
    while True:
        s=input("please input: ")
        mess.Send(sock,s)'''

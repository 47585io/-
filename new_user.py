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


class Graphics:
    '''you can use it, also can not use'''

    def set_input(self, size=(0, 0)):
        self.entfarme = tk.Frame(self.bgfarme, background=self.Color["bg"])
        self.xscro = tk.Scrollbar(self.entfarme, orient=tk.HORIZONTAL)
        self.ent = tk.Entry(self.entfarme, xscrollcommand=self.xscro.set)
        self.xscro.config(command=self.ent.xview)
    # after set, please pack them, self.entfarme.place()   self.ent.pack()   self.xscro.pack(fill=tk.X)

    def set_win(self, win):
        '''quick config a win'''
        win.config(bg=self.Color["bg"],)
        win.title("Sock QQ")
        win.geometry(self.geosize())
        win.resizable(0, 0)
        #win.protocol('WM_DELETE_WINDOW',lambda :exit(0))
        self.bgfarme = tk.Frame(
            win, background=self.Color["bg"], width=self.Win_Size[0][0], height=self.Win_Size[0][1])
        self.bgfarme.pack()
        self.bgfarme.update()
    # olny set once

    def set_friends(self, friends):
        '''show friend_list'''
        while len(self.friend_list_but) < len(friends):
            self.friend_list_but.append(tk.Button())
        i = 0
        while i < len(self.friend_list_but):
            self.friend_list_but[i].config(
                text=friends[i], bg=str(random.randint))

    def Canvas_mess(Canvas):
        Canvas.g()

    def talk_lab():
        pass

    def Canvas_config(self,):
        '''quick config a have scro Canvas'''
        self.can_yscro = tk.Scrollbar(self.bgfarme,)
        self.can = tk.Canvas(self.bgfarme, yscrollcommand=self.can_yscro.set)
        self.can_yscro.config(command=self.can.yview)

    def welcome(self):
        self.lbtmp.config(
            text="Welcome!", foreground=self.Color["fg"], background=self.Color["bg"])
        self.lbtmp.place(x=0, y=0)
        self.bgfarme.update()

    def init(self, win) -> None:
        '''set you config'''
        # On the this Label going to use many count, but their olny sava once
        # And below, that lab have many, but their going to while use
        self.friend_list_but = []
        self.Win_Size = [(360, 450, 1600, 1000)]
        self.Color = {"bg": "#282c34", "fg": "#abb2bf"}
        self.Font = {"cute": ()}
    # the diffrent page win size
# why i am not use __init__? because the lab must after init window
        self.set_win(win)
        self.lbtmp = tk.Label(self.bgfarme)
        self.welcome()

    def geosize(self, tup=None):
        if tup:
            return str(tup[0][0])+"x"+str(tup[0][1])+"+"+str(tup[0][2])+"+"+str(tup[0][3])
        return str(self.Win_Size[0][0])+"x"+str(self.Win_Size[0][1])+"+"+str(self.Win_Size[0][2])+"+"+str(self.Win_Size[0][3])

    def quickconfig(self, win):
        '''you can call it use defalut config'''
        self.init(win)

        # self.bgfarme.destroy()
    def First_Page(self, win):

        pass

    def Second_Page(self):
        pass

    def Third_Page(self):
        pass


GNU = Graphics()


def main(mess, sock, friends, gra):
    mess.Send(sock, "LOGIN "+USER_NAME)
    r = th.Thread(target=mess.Read, args=(sock,))
    r.start()
    sleep(1)
    win = tk.Tk()
    gra.quickconfig(win)
    win.mainloop()


main(UDP, UDP_SOCK, Friend_List, GNU)


'''
mess.Send(sock, "LOGIN H")
    friends.addfriend(mess,sock)
    while True:
        s=input("please input: ")
        mess.Send(sock,s)'''

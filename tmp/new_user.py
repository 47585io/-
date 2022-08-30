from copy import deepcopy
from inspect import isabstract
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
from concurrent.futures import ThreadPoolExecutor as Th

Mess_Buffer = 1024
Max_Mess = 10
# recv buffer size and user mess_list size
UDP_SOCK = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
UDP_SOCK.bind(("", 0))
TCP_SOCK = socket.socket()
TCP_SOCK.bind(("", 0))
# the user, olny two port with server
USER_NAME = "cat"
Is_Loding = 1
# ...
MAX_THD = 1
# the user can use every work max thread


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
        return

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
        global USER_NAME
        while 1:
            tmp = sock.recvfrom(1024)
            self.MessCache.append(tmp[0])
            self.index += 1
            self.yes = 1
            if(self.index >= Max_Mess):
                del self.MessCache[0]
                self.index -= 1
            print("i am ",USER_NAME,"a new mess: ",tmp[0],"\n")


UDP = UDP_Mess()
# mess object


class friends:
    '''The all friend save in'''

    def __init__(self) -> None:
        self.friend_list = ["my shadow", "my computer",]
        # friend name list
        self.pic = ['/home/tom/vscode/github/--1/new.png',
                    '/home/tom/vscode/github/--1/furry.gif']
        self.Mess_Friend = {}
        # 记录与好友通信mess label
        self.talk_with = []
        # who talk with now
        self.tmp = []

    def show(self):
        return self.friend_list

    def format_list(self, list_):
        '''format the going to add list'''
        new_list = []
        for name in list_:
            if name not in self.friend_list:
                new_list.append(name)
        return new_list

    def __from_server_get_friend_list(self, mess, sock):
        '''func name is i mean'''
        mess.Send(sock, "AddFriend ")
        s_str = mess.getnew()
        return Spilt_Mess.Friend_list_Read_Spilt(s_str)

    def addfriend(self, mess=UDP, sock=UDP_SOCK):
        '''get friend list and show'''
        list_ = self.__from_server_get_friend_list(mess, sock)
        list_ = self.format_list(list_)
        self.tmp.extend(list_)

    def Search_Friend(self, name_str):
        '''search name_str in friend_list'''
        if not name_str:
            #print("no name")
            return
        name_list = []
        for i in self.tmp:
            if i.find(name_str) != -1:
                name_list.append(i)
        return name_list


Friend_List = friends()
# sava all friend


class Spilt_Mess:
    '''i have a many spilt str func'''
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
    '''the welcom class, can show welcome page and switch page'''
    LAB_Count = 3
    BUT_Count = 3
    # lab and but init count

    def __init__(self) -> None:
        self.func = []
        self.index = 0
        self.Win_Size = [(360, 450, 1600, 1000)]
        self.Color = {"bg": "#282c34", "fg": "#abb2bf",
                      "entblock": "#808080", "ffg": "#484848"}
        self.Font = {"zheng": "DejaVu Sans", "alpha": "Quicksand",
                     "drak": "Quicksand Medium", "small": "Z003",
                     "beutful": "DejaVu Math TeX Gyre", "frmory": "Dingbats"}
        self.Font_size = {"small": 5, "mid": 10, "big": 20}
        self.pic_size = [100, 90]
        self.filename = ""
        self.cache1 = greenlet.greenlet(self.new)

    def init(self,):
        self.win = tk.Tk()
        self.bgfarme = tk.Frame(self.win)
        self.lab_list = []
        self.but_list = []
        self.message = tk.Message(self.bgfarme)
        # the index 1 is myself but,please no search
        for i in range(self.LAB_Count):
            self.lab_list.append(tk.Label(self.bgfarme))
        for i in range(self.BUT_Count):
            self.but_list.append(tk.Button(self.bgfarme))

    def init_ent(self):
        '''quick init a ent'''
        self.entfarme = tk.Frame(self.bgfarme)
        self.ent_scro = tk.Scrollbar(self.entfarme)
        self.ent = tk.Entry(self.entfarme,)
# on, please wirte all init fun on

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
            self.index += 1
            self.func.append(src_fun)
        go_fun()

    def retu(self,):
        '''pop and call a fun from func'''
        if self.index < 1:
            exit(0)
        self.clear()
        self.but_list[1].pack(anchor="nw")
        self.index -= 1
        fun = self.func[self.index]
        fun()
        #print("finsh!")
        del self.func[self.index]

    def winconfig(self):
        self.win.config(bg=self.Color["bg"],)
        self.win.title("Sock QQ")
        self.win.geometry(self.geosize())
        #self.win.resizable(0, 0)
        self.bgfarme.config(
            background=self.Color["bg"], width=self.Win_Size[0][0], height=self.Win_Size[0][1])
        self.bgfarme.pack()
        # olny set once

    def labconfig(self, lab_list):
        for lab in lab_list:
            lab.config(anchor="nw", font=(self.Font["zheng"], self.Font_size["mid"]),
                       borderwidth=0, foreground=self.Color["fg"], background=self.Color["bg"], width=int(self.Win_Size[0][0]/self.Font_size["mid"]))

    def butconfig(self, but_list):
        for but in but_list:
            but.config(font=(self.Font["zheng"], self.Font_size["mid"]),
                       borderwidth=0, highlightthickness=0, activebackground=self.Color["ffg"],  foreground=self.Color["fg"], activeforeground=self.Color["fg"], background=self.Color["bg"])

    def ent_config(self):
        self.entfarme.config(background=self.Color['bg'],)
        self.ent_scro.config(command=self.ent.xview, background=self.Color['fg'],
                             activebackground=self.Color["entblock"], borderwidth=0, orient=tk.HORIZONTAL, elementborderwidth=0, activerelief="sunken")
        self.ent.config(xscrollcommand=self.ent_scro.set, borderwidth=1, highlightbackground=self.Color['fg'],
                        highlightcolor=self.Color['fg'],
                        highlightthickness=1, insertbackground='#61afef',
                        bd=0, background=self.Color['bg'], fg=self.Color['fg'],)
        self.ent.pack(side='top')
        self.ent_scro.pack(side="bottom", fill=tk.X)
# please wirte all config fun on

    def quickconfig(self,mess,sock):
        '''usally, user olny call it, can init and config all lab'''
        self.sock=sock
        self.mess=mess
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
        if self.openfile() == 0:
            self.go(self.welcome1)
        else:
            USER_NAME = tmp[0][0:-1:]
            self.filename = tmp[1]
            self.go(self.Login)
        self.win.mainloop()
        self.new()
        self.win.mainloop()

    def welcome1(self):
        self.lab_list[0].config(text="\nWelcome!", font=(
            self.Font["zheng"], 20, "bold"))
        self.lab_list[0].pack()
        self.lab_list[1].config(text="\n\n一切刚刚好,现在立刻!\n\n",)
        self.lab_list[1].pack()
        self.but_list[0].config(
            text="Get Started", command=lambda: self.go(self.welcome2, self.welcome1))
        self.but_list[0].pack(side='right')
        #print("call!")

    def welcome2(self):
        #print("&")
        self.lab_list[0].config(text='\nSet Name',)
        self.lab_list[0].pack()
        self.lab_list[1].config(text="\n伟大的名字\n ")
        self.lab_list[1].pack()
        self.entfarme.pack()
        self.lab_list[2].config(text="\n")
        self.lab_list[2].pack()
        self.but_list[0].config(text="Enter", command=lambda: self.go(
            self.welcome3, self.welcome2, self.setname))
        self.but_list[0].pack(side='right')
        self.ent.bind("<Return>", lambda k, x=self.welcome3,
                      y=self.welcome2, z=self.setname: self.go(x, y, z))

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
        self.but_list[2].config(command=lambda: self.go(
            self.Login, self.welcome3, self.save), text="Login")
        self.but_list[2].pack(side='right')
    #on, three welcome page is really, after, you must set a login func

    def choose(self,):
        '''open file chooser'''
        self.filename = fid.askopenfilename(
            initialdir="../", filetypes=[("PNG", "*.png"), ("JPG", "*.jpg"), ("GIF", "*.gif"), ])
        if self.filename != ():
            self.message.config(text=self.filename+"\n")

    def save(self):
        '''save user choose picture'''
        if self.filename == ():
            return
        if self.filename.find(".jpg"):
            newfile = Image.open(self.filename, "r")
            newfile.thumbnail((300, 700))
            newfile.convert("RGBA")
            mk = os.path.abspath("./")
            newfile.save(mk+"/new.png")
            self.filename = mk+"/new.png"
            print("save in ", mk+"/new.png")
        file = open("name.txt", "w")
        file.writelines([USER_NAME+"\n", self.filename])
        file.close()

    def setname(self):
        global USER_NAME
        USER_NAME = self.ent.get()
        print("hello", USER_NAME)

    def openfile(self):
        '''check a can use file'''
        file = open("name.txt", "r+")
        tmp = file.readlines()
        if tmp == []:
            return 0
        else:
            return tmp

    def Login(self):
        '''after login, going to show friends'''
        global USER_NAME
        self.lab_list[0].config(text='\nHello!\n', anchor='center', font=(
            self.Font["zheng"], self.Font_size["big"], "bold"))
        self.lab_list[0].pack()
        self.message.config(text=USER_NAME, anchor="nw", font=(self.Font["zheng"], self.Font_size["mid"]),
                            foreground=self.Color["fg"], background=self.Color["bg"], width=self.Win_Size[0][0]-20)

        self.furry = tk.PhotoImage(file=self.filename)
        self.lab_list[1].config(width=self.pic_size[0],
                                height=self.pic_size[1], image=self.furry)
        self.lab_list[1].pack()
        self.lab_list[2].pack()
        self.message.pack()
        self.win.update()
        self.mess.Send(self.sock, "LOGIN "+USER_NAME)
        for i in range(MAX_THD):
            r = th.Thread(target=self.mess.Read, args=(self.sock,))
            r.setDaemon(True)
            r.start()
        sleep(2)
        self.cache1.switch()
        #jump to show

    def new(self):
        '''this is Extended access'''
        pass

# the class can't to new class!!!!
# or, try lab?
# no! it not have scro!
# but we must 继承 baseclass, 否则 all lab going to del
# why?.... why not?


class Friend_list(Welcome):
    '''the friend_list class, can show your friend_list and add new friend'''
    def __init__(self) -> None:
        Welcome.__init__(self)
        self.Canv_x = 0  # 50
        self.Canv_y = 0  # 45
        self.Canv_x_from = 70
        self.Canv_size = (self.Win_Size[0][0], self.pic_size[1])
        self.s = th.Thread(target=self.search,)
        self.s.setDaemon(True)
        self.isstart=0
    def init(self,):
        '''redefine func'''
        Welcome.init(self)
        self.canv_init()
        self.furry_l = []
        self.tag_list = []
        self.List = tk.Listbox(self.bgfarme, background=self.Color['bg'], selectbackground=self.Color['ffg'],
                               foreground=self.Color['fg'], selectforeground=self.Color['fg'], borderwidth=0, highlightthickness=0)

    def canv_init(self,):
        '''init canv and scro'''
        self.f_can = tk.Canvas(self.bgfarme, highlightthickness=0, scrollregion=(
            0, 0, 500, 1000), confine=False, background=self.Color['bg'], selectbackground=self.Color['ffg'], selectforeground='white', borderwidth=0,)
        self.f_scro = tk.Scrollbar(self.bgfarme)

    def canvconfig(self, canv, scro):
        '''config a Canv with Theme'''
        canv.config(width=self.Win_Size[0][0], height=self.Win_Size[0]
                    [1], borderwidth=0, yscrollcommand=scro.set,)
        scro.config(command=canv.yview, background=self.Color['fg'],
                    activebackground=self.Color["entblock"], borderwidth=0, elementborderwidth=0, activerelief="sunken")

    def quickconfig(self, friends,sock,mess):
        '''redefine func'''
        Welcome.quickconfig(self,mess,sock)
        self.canvconfig(self.f_can, self.f_scro)
        self.fren = friends

    def new(self):
        '''redefine last class func, go to show friends'''
        self.go(self.showfriends)
        pass

    def place_forgets(self, *arg):
        '''it going to place_forget all arg as mid fun before pack_forget'''
        for a in arg:
            a.place_forget()

    def draw_a_friend(self, canv, name, pic, relapos, namepos, picpos,func):
        '''draw a friend mess'''
        tag = canv.create_rectangle(relapos[0], relapos[1], relapos[2], relapos[3],
                                    activefill=self.Color['ffg'], outline=self.Color['bg'], width=0)
        if canv:   
            canv.create_image(picpos[0], picpos[1], image=pic,)
        canv.create_text(namepos[0], namepos[1],
                         text=name, fill=self.Color['fg'], font=(self.Font["zheng"], self.Font_size["mid"]))
        self.f_can.tag_bind(tag, '<Button-1>',func)
        self.tag_list.append(tag)

    def showfriends(self):
        self.but_list[0].config(text="+", command=self.addfriend_mid)
        self.but_list[0].place(x=self.Win_Size[0][0]-40, y=0)
        self.f_scro.pack(fill=tk.Y, side='right')
        self.f_can.pack()
    # but_list[0] is place!!!

        if self.fren.pic == []:
            self.f_can.create_text(self.Win_Size[0][0]//2, self.Win_Size[0][1]//2-50, fill=self.Color['fg'], font=(
                self.Font["zheng"], self.Font_size["big"], "bold"), text="No Friends!")

        count = len(self.furry_l)
        while count < len(self.fren.show()):
            if count>=len(self.fren.pic):
                self.furry_l.append(tk.PhotoImage(
                    file="default.png", width=self.pic_size[0], height=self.pic_size[1]))
            else:
                self.furry_l.append(tk.PhotoImage(
                file=self.fren.pic[count], width=self.pic_size[0], height=self.pic_size[1]))
            count += 1
        for count in range(len(self.furry_l)):
            if count % 2 == 0:
                self.draw_a_friend(
                    self.f_can, self.fren.friend_list[count], self.furry_l[count], (self.Canv_x, self.Canv_y, self.Win_Size[0][0], self.Canv_y+self.pic_size[1],), (self.Canv_x+self.pic_size[0]+self.Canv_x_from, self.Canv_y+self.pic_size[1]//2,), (self.Canv_x+50, self.Canv_y+45,),self.talk_with_mid)
            if count % 2 == 1:
                self.draw_a_friend(
                    self.f_can, self.fren.friend_list[count], self.furry_l[count], (self.Canv_x, self.Canv_y, self.Win_Size[0][0], self.Canv_y+self.pic_size[1],), (self.Win_Size[0][0]-self.pic_size[0]-self.Canv_x_from, self.Canv_y+self.pic_size[1]//2,), (self.Win_Size[0][0]-self.pic_size[0]+50, self.Canv_y+45,),self.talk_with_mid)
            count += 1
            self.Canv_y += self.pic_size[1]
            #print(self.Canv_x)

    def addfriend_mid(self):
        '''clear Canv and go to add friend'''
        self.clear_Canv()
        self.go(self.addfriend, self.showfriends,
                lambda: self.place_forgets(self.but_list[0]))

    def addfriend(self):
        '''config a addfriend page'''
        self.entfarme.pack()
        self.List.pack()
        self.but_list[2].pack(side='right')
        self.but_list[2].config(text='Ok', font=(self.Font["zheng"], self.Font_size['mid']+3,),command=self.addmany)
        self.fren.addfriend()
        if self.isstart==0:
            self.isstart+=1
            self.s.start()
            
    def search(self):
        '''search user input str in friend_list'''
        tmp = ""
        while 1:
            if tmp == self.ent.get():
                continue
            tmp = self.ent.get()
            if not tmp:
                self.List.delete(0, "end")
            list = self.fren.Search_Friend(self.ent.get())
            if list:
                self.List.delete(0, "end")
                for l in list:
                    self.List.insert("end", l)
            else:
                self.List.delete(0, "end")
    def addmany(self):
        '''user going to add friend'''
        try:
            tup = self.List.get(self.List.curselection())
            self.fren.friend_list.append(tup)
        except Exception:
            pass
    def clear_Canv(self,):
        '''clear Canv on old page'''
        self.f_can.delete(tk.ALL)
        self.index = 0
        self.tag_list.clear()
        self.Canv_y = 0
        #self.furry_l.clear()

    def talk_with_mid(self, event):
        '''user choose which friend'''
        #print(event.widget, event.x, event.y)
        tup = self.f_can.find_closest(event.x, event.y)
        index = self.tag_list.index(tup[0])
        name = self.fren.friend_list[index]
        self.clear_Canv()
        self.go(lambda: self.talk_with(name), self.showfriends,
                lambda: self.place_forgets(self.but_list[0]))

    def talk_with(self, name):
        print(name)
    # please redefine after

class Talk_with(Friend_list):
    '''the talk_with class, can talk with your friens or grounp, and send or get file'''
    def __init__(self) -> None:
        Friend_list.__init__(self)
        self.talk=th.Thread(target=self.Readshow,)
        self.talk.setDaemon(True)
        self.istalk=0
    def init(self):
        Friend_list.init(self)
    def quickconfig(self,friends,mess,sock):
        Friend_list.quickconfig(self,friends,sock,mess)
        #self.mess=mess
        #self.sock=sock
    def talk_with(self, name):
        if self.istalk==0:
            self.talk.start()
            self.istalk+=1
        #self.but_list[1].config(command=self.endretu)
        self.fren.talk_with=name
        self.f_can.pack()
        self.entfarme.pack(side='left',anchor='nw')
        self.but_list[2].config(text='send',command=lambda :self.Sendshow(0,name))
        self.but_list[2].pack(side='left')
        self.ent.bind("<Return>",lambda x,y=name:self.Sendshow(x,y))
    def Sendshow(self,tmp,name):
        global USER_NAME
        s_str=self.ent.get()       
        self.mess.Send(self.sock,s_str,name)
        self.draw_a_friend(self.f_can,s_str,self.furry_l[0],
                           (self.Canv_x, self.Canv_y, self.Win_Size[0][0], self.Canv_y+self.pic_size[1]-20,),  (self.Win_Size[0][0]-self.pic_size[0]-self.Canv_x_from, self.Canv_y+self.pic_size[1]//2,), (self.Win_Size[0][0]-self.pic_size[0]+50, self.Canv_y+45,),self.delmess)
        self.Canv_y+=self.pic_size[1]
    def Readshow(self):
      while 1:
        s_str=self.mess.get()
        if s_str:
            s,name=Spilt_Mess.Read_spilt(s_str)
            print("this ",name,"   ",s,"\n")
            if name==self.fren.talk_with:
                i=self.fren.friend_list.index(name)
                self.draw_a_friend(self.f_can, s, None, (self.Canv_x, self.Canv_y, self.Win_Size[0][0], self.Canv_y+self.pic_size[1],), (self.Canv_x+self.pic_size[0]+self.Canv_x_from, self.Canv_y+self.pic_size[1]//2,), (self.Canv_x+50, self.Canv_y+45,), self.delmess)
                self.Canv_y += self.pic_size[1]
    def delmess(self):
        pass
    def endretu(self):
        self.clear_Canv()
        self.retu()
    
class Graphics(Talk_with):
    def __init__(self) -> None:
        Talk_with.__init__(self)
        pass
    

GNU = Talk_with()


def main(mess, sock, friends, gra):
    gra.quickconfig(friends,mess,sock)
    gra.run()


main(UDP, UDP_SOCK, Friend_List, GNU)


'''
mess.Send(sock, "LOGIN H")
    friends.addfriend(mess,sock)
    while True:
        s=input("please input: ")
        mess.Send(sock,s)'''

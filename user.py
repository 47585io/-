import socket
from termios import FF1
import threading as thr
import tkinter as tk
import atexit
from PIL import Image

def whenexit():
    sock.sendto("EXIT".encode(), ("127.0.0.1", 1234))
    sock.recvfrom(200)
    sock.close()
atexit.register(whenexit)


sock=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sock.bind(("",0))
addr,port=sock.getsockname()

def Send():
    while 1:
        s_str = "127.0.0.1&1236$"
        s_str+=input()
        sock.sendto(s_str.encode(),("127.0.0.1",1234))
def Read():
    while 1:
        tmp=sock.recvfrom(256)
        print(tmp[0].decode())
        
class Graphics: 
    
    def Isin(self,win):
        file=open("/home/tom/vscode/idea/socqq/name.txt","w")
        file.close()
        file = open("/home/tom/vscode/idea/socqq/name.txt", "r")
        name=file.read()
        file.close()
        
        sock.sendto(str("LOGIN "+name).encode(), ("127.0.0.1", 1234))
        tmp=sock.recvfrom(200)
        r = thr.Thread(target=Read)
        r.start()
        if tmp[0].decode()=="":
            self.new_page(win)
            print("new")
        else:
            self.back_page(win)
            print("old")
    def back_page(self,win):
        win.geometry("210x240")
        win.title("欢迎!")
        win.config(background="#C0D9D9")
        
        self.pic = tk.PhotoImage(file="/home/tom/vscode/github/--1/furry.gif")
        self.lab=tk.Label(win,text="hh",image=self.pic,activebackground='green',width=80,height=60)
        self.lab.place(x=65,y=10)  
        #self.friend_list()
    def new_page(self,win):
        self.back_page(win)  
        f1=tk.LabelFrame(win)
        f1.place(x=20,y=110)
        ent = tk.Entry(f1, text="a new name",selectbackground="green",width=13)
        ent.pack()
        but=tk.Button(text="login",)
        but.place(x=150,y=200)
#get and write to file
    def friend_list(self,win):
        tmp = thr.Thread(target=self.talk_with)
        pass
    def talk_with(self):
        win=tk.Tk()
        win.mainloop()
        pass
    
def start():   
    user=Graphics()
    win=tk.Tk()
    user.Isin(win)
    win.mainloop()
    
s=thr.Thread(target=Send)
s.start()

start()
    



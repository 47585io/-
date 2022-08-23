from fileinput import filelineno
import socket
from termios import FF1
import threading as thr
from time import sleep
import tkinter as tk
import atexit
from PIL import Image

def whenexit():
    sock.sendto("EXIT".encode(), ("127.0.0.1", 1234))
    sock.recvfrom(200)
    sock.close()
atexit.register(whenexit)

class friends:
    def __init__(self) -> None:
        my_name=""
        self.friend_list=[]
        self.now_talk_with=[]
my=friends()


sock=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sock.bind(("",0))
addr,port=sock.getsockname()

def Send(send_str,to_user=""): 
    to_user_str=""
    if to_user!="":
        to_user_str=str(to_user[0])+str(to_user[1])
    send_str = to_user_str+send_str
    sock.sendto(send_str.encode(),("127.0.0.1",1234))
def Read():
    while 1:
        tmp=sock.recvfrom(256)
        print(tmp[0].decode())
        
class Graphics:   
    def Isin(self,win):
        file = open("/home/tom/vscode/idea/socqq/name.txt", "r+")
        name=file.read()   
        Send(str("LOGIN "+name))
        tmp=sock.recvfrom(200)
        r = thr.Thread(target=Read)
        r.start()
        if tmp[0].decode()=="":
            self.new_page(win,file)
            print("new")
        else:
            self.back_page(win)
            print("old")
            file.close()
        #self.friend_list()
            
    def back_page(self,win):
        win.geometry("210x240")
        win.title("欢迎!")
        win.config(background="#C0D9D9")
        
        self.pic = tk.PhotoImage(file="/home/tom/vscode/github/--1/furry.gif")
        self.lab=tk.Label(win,text="hh",image=self.pic,activebackground='green',width=80,height=60)
        self.lab.place(x=65,y=10) 
        self.friend_list(win)
        self.lab.place_forget()
    def new_page(self,win,file):
        self.back_page(win)  
        f1=tk.LabelFrame(win)
        f1.place(x=20,y=110)
        self.ent = tk.Entry(f1, text="a new name",selectbackground="green",width=13)
        self.ent.pack()  
        
        def login():
            Send("LOGIN "+self.ent.get())
            buf = self.ent.get()
            file.write(buf)
            file.close()
            
        self.but = tk.Button(text="login", command=login)
        self.but.place(x=150,y=200) 
        sleep(2)  
        f1.place_forget()     
        self.ent.pack_forget()
        self.but.place_forget()
#get and write to file
    def friend_list(self,win):
        fa1=tk.Frame(win,width=210,height=240,background='#C0D9D9')
        fa1.place(x=0,y=0)
        win.update()
        
        #tmp = thr.Thread(target=self.talk_with)
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
    

start()
    



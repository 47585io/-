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
        self.friend_list={}
        self.now_talk_with=[]
        
    def spilt(self,src):
        addr=""
        name=""
        port=0
        lis=[]
        i=2
        j=0
        while 1:
            if(src[i]=="["):
                break
            i+=1
        while i<len(src):
            
                    if src[i] == "(":
                        j=i
                        while 1:
                            if src[i]==")":
                #have a 'jdkdr', ('127.0.0.1', 56599)
                                lis.append(src[j+1:i+1:])
                                break
                            i+=1
                        print(lis[0])
                    
                    if src[i]=="]":
                        break
                    i+=1
            
        for l in lis:
            i = 0
            j = 1
            while i < len(l):
                if l[i]=="'":
                    name=l[j:i:]
                    #print(name)
                    #hava a name,jdkdr
                if l[i]=="(":
                    #hava after ('127.0.0.1', 56599)
                    i+=2
                    #have 127.0.0.1', 56599)
                    j=i
                    while 1:
                        i+=1
                        if l[i]=="'":
                            addr=l[j:i:]
                            #print(addr)
                            #have 127.0.0.1
                        if l[i]==",":
                            i+=1
                            #the after  56599)
                            j=i
                        if l[i]==")":
                            #have 56599
                            port=int(l[j:i:])
                            #print(port)
                            break                           
                i+=1
            if addr==my_addr and port ==my_port:
                pass
            self.friend_list[name]=(addr,port)
                     
    def addfriend(self): 
      try:
        Send("ADDfriend",)
        tmp=sock.recvfrom(1024)  
        print(tmp[0])
        if not tmp[0]:
            return
        self.spilt(tmp[0].decode())
        print(self.friend_list)
      except Exception:
          print("hggh")
       
        
my=friends()


sock=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sock.bind(("",0))
my_addr,my_port=sock.getsockname()


def Send(send_str,to_user=""):
    sock_count=0 
    to_user_str=""
    if to_user!="":
        to_user_str=to_user
    send_str = to_user_str+send_str
    while sock_count<len(send_str):
        sock_count+=sock.sendto(send_str.encode(),("127.0.0.1",1234))
def Read():
    while 1:
        tmp=sock.recvfrom(1024)
        print(tmp[0].decode())
        
class Graphics:   
    def Isin(self,win):
        file = open("/home/tom/vscode/idea/socqq/name.txt", "r+")
        name=file.read()   
        Send(str("LOGIN "+name))
        tmp=sock.recvfrom(200)
        
        if tmp[0].decode()=="":
            self.new_page(win,file)
            print("new")
        else:
            self.back_page(win)
            print("old")
            sleep(2)
            self.friend_list(win)
            
            file.close()
            self.lab.place_forget()
        #self.friend_list()
            
    def back_page(self,win):
        win.geometry("210x240")
        win.title("欢迎!")
        win.config(background="#C0D9D9")
        
        self.pic = tk.PhotoImage(file="/home/tom/vscode/github/--1/furry.gif")
        self.lab=tk.Label(win,text="hh",image=self.pic,activebackground='green',width=80,height=60)
        self.lab.place(x=65,y=10) 
        
    def new_page(self,win,file):
        self.back_page(win)  
        f1=tk.LabelFrame(win)
        f1.place(x=20,y=110)
        ent = tk.Entry(f1, text="a new name",selectbackground="green",width=13)
        ent.pack()  
        
        def login():
            Send("LOGIN "+ent.get())
            buf = ent.get()
            file.write(buf)
            file.close()
            f1.place_forget()
            ent.pack_forget()
            but.place_forget()
            self.friend_list(win)
            
        but = tk.Button(text="login", command=login,background="#C0D9D9",activebackground='pink')
        but.place(x=150,y=200)  
       
#get and write to file
    def friend_list(self,win):
        fa1 = tk.Frame(win, background='#C0D9B9',width=210,height=40)
        fa1.pack()
        if not my.friend_list:
            self.lab.config(text="No Friens!",font=(30,),background="#C0D9D9")
            self.lab.pack()
        add=tk.Button(text="add friends",command=my.addfriend)
        add.pack()
        win.update()
        #self.talk_with()
        
        input()
        r = thr.Thread(target=Read)
        r.start()
        
        tmp = thr.Thread(target=self.talk_with)
        tmp.start()
        
    def talk_with(self):
        #win=tk.Tk()
        
       # win.mainloop()
        while 1:   
            s=input()  
            print("input")
            for user in my.friend_list.keys():  
                print("key")
                to= str(my.friend_list[user][0])+"&"+str(my.friend_list[user][1])+"$"
                print("to")
                Send(s,to)
            
    
def start():   
    user=Graphics()
    win=tk.Tk()
    user.Isin(win)
    win.mainloop()
    

start()
    



from fileinput import filelineno
from os import TMP_MAX
import socket
from termios import FF1
import threading as thr
from time import sleep
import tkinter as tk
import atexit
from PIL import Image

class mess:
    def __init__(self) -> None:
        self.MessCache=[]
        self.index=-1
        self.NewMess=""
        self.yes=0
    def get(self):
        while 1:
            if self.yes:
                self.NewMess = self.MessCache[self.index]
                del self.MessCache[self.index]
                self.index-=1
                self.yes=0
                return self.NewMess

    def Send(self,send_str, to_user=None):
        sock_count = 0  
        if to_user:     
            send_str = str(to_user[0])+"&" + str(to_user[1])+"$"+send_str
        while sock_count < len(send_str):
            sock_count += sock.sendto(send_str.encode(), ("127.0.0.1", 1234))

    def Read(self):
        while 1:
            tmp = sock.recvfrom(1024)
            self.MessCache.append(tmp[0].decode())
            self.yes=1
            self.index+=1
            if(self.index>=10):
                del self.MessCache[0]
                self.index-=1
            print(tmp[0])
            #print(self.talk_spilt(tmp[0]))

    @staticmethod    
    def talk_spilt(str):
      try:
        i=0
        while str[i]!="$":
            i+=1
        i+=1
        new_str=str[i::]        
        return new_str
      except Exception:
        print("error talk")

My_Mess=mess()   
    
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
        self.have_friend={}
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
            if addr!=my_addr and port !=my_port:
                self.friend_list[name]=(addr,port)
                     
    def addfriend(self): 
      try:
        My_Mess.Send("ADDfriend",)
        tmp=My_Mess.get()
        print(tmp[0])
        if not tmp[0]:
            return
        self.spilt(tmp)
        print(self.friend_list)
      except Exception:
          print("hggh")
       
        
my=friends()


sock=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sock.bind(("",0))
my_addr,my_port=sock.getsockname()
my_name=""


class Graphics:   
    def Isin(self,win):
        global my_name
        win.resizable(0,0)
        file = open("/home/tom/vscode/idea/socqq/name.txt", "r+")
        my_name=file.read()   
        My_Mess.Send(str("LOGIN "+my_name))
        tmp=My_Mess.get()
        
        if tmp=="":
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
            My_Mess.Send("LOGIN "+ent.get())
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

    def addfriend(self,win):
        my.addfriend()
        self.list=tk.Listbox(win)
        self.list.place(x=0,y=0)
        i=0
        for name in my.friend_list.keys():
            self.list.insert(i,name)
            i+=1
        def Return():
            self.list.place_forget()
            self.but.place_forget()    
            
        self.but =tk.Button(text="return",command=Return)
        self.but.place(x=130,y=200)
        def Ok():
            self.labgroup=[]
            
            self.labgroup.append
        self.enter=tk.Button(text="Ok",command=Ok)
            
    def friend_list(self,win):
        fa1 = tk.Frame(win, background='#C0D9B9',width=210,height=50)
        fa1.pack()
        lab=tk.Label(fa1,image=self.pic,
                            activebackground='green',width=60,)
        lab.place(x=0,y=0)
        lab2=tk.Label(fa1,font=(0,8),text=my_name+"localhost"+" "+str(my_port),background='#C0D9B9',width=12,anchor="nw",)
        lab2.place(x=60,y=0)
        if not my.friend_list:
            lab3=tk.Label(text="No Friends!",font=(30),background="#C0D9D9",height=4)
            lab3.pack()
        
        menu = tk.Menu(win, tearoff=False, activebackground="pink")
        menu.add_command(label="add",command=lambda :self.addfriend(win))
        def pos(enxy):
            menu.post(enxy.x_root, enxy.y_root)
        win.bind("<Button-3>", pos)
        
        win.update()
        #self.talk_with()
        input("按任意键开始")
        s=thr.Thread(target=self.talk_with)
        s.start()
        
    def talk_with(self):
        #win=tk.Tk()       
       # win.mainloop()
        for user in my.friend_list.keys():
           to = my.friend_list[user]
           My_Mess.Send("init", to)
        while 1:   
            s=input()  
            print("input",)
            if s!="" or s:
              for user in my.friend_list.keys():  
                print("key",)
                to= my.friend_list[user]
                print("to")
                My_Mess.Send(s,to)
                
            
    
def start():  
    r = thr.Thread(target=My_Mess.Read)
    r.start()
    user=Graphics()
    win=tk.Tk()
    user.Isin(win)
    win.mainloop()
    

start()
    



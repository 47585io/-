import socket
import threading as thr
import tkinter as tk
import atexit

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
    def new_page(self,win):
        pass
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
        '''
        win.geometry("400x150")
        win.title("欢迎!")
        f1=tk.Frame(win,background='blue',width=150,height=50)
        f1.pack()
        f2=tk.Frame()
        name = ""
        self.ent=tk.Entry(win)
        
        sock.sendto("LOGIN "+str(name).encode(), ("127.0.0.1", 1234))
        self.friend_list()
        pass
        '''
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
    



from email.iterators import typed_subpart_iterator
import socket
from concurrent import futures as fu
import multiprocessing as mut
import atexit
PRO_MAX=1

def whenexit():
    sock.close()
#for all now_in user send exit
atexit.register(whenexit)

class users:
    def __init__(self):
        self.users = {}
        #{name:(addr,port)}
        self.users_friend = {}
        #{name:[friendname]}
        self.now_in=[]
        #[(now_in_addr,port)]
    
    def Login(self,tup):
        name=tup[0][6::].decode()
        if name=='':
            return ''
        print(name)     
        if tup not in self.users.values():
            self.users[name]=tup[1]
        if name not in self.users_friend:
            self.users_friend[name]=[]
        if name not in self.now_in:
            self.now_in.append(self.users[name])
        name+=str(self.users_friend[name])
        return name
    def Isin(self,tup):
        for i in self.now_in:
            pass
        if tup[0] in self.now_in:
            return 1
        return 0
    def addfriend(self,tup):
        if self.users.values() == tup[1]:
            return ""
        addr=str(self.users.items())       
        return addr

def str_to_tuple(src):
    src = src.decode()
    a = ""
    tup = []
    addr = ""
    port = 0
    for h in src:
        if(h == '&'):
            addr = a
            a = ""
            continue
        if(h == '$'):
         #if str has many ' ', so it was bug, bucase it eat all ' '
            port = int(a)
            a = ""
            continue
        a += h
    tup.append((addr, port))
    tup.append(a)
    return tup

def tuple_to_str(tup):
    return str(tup[0])+"&"+str(tup[1])+"$"

class message:
    def __init__(self):
        self.cache = []
        self.buffer = []

    def bbmess(self,tmp):
        if tmp[0].decode().startswith("LOGIN"):
            what=usr.Login(tmp)
            lis=[tmp[1],what]
            return lis
        elif tmp[0].decode().startswith("ADDfriend"):
            addr=usr.addfriend(tmp)
            lis=[tmp[1],addr]
            return lis
        else:
            lis = str_to_tuple(tmp[0])
            src_lis=tuple_to_str(tmp[1])
            lis[1]=src_lis+lis[1]
            if usr.Isin(lis):
                return lis
            lis=(tmp[1],"no user!")
            return lis
    
    def talk_to(self,sock):
        while 1:
            sock_count=0
            tmp = sock.recvfrom(1024)
            print("接收", tmp[0])
            lis=self.bbmess(tmp)            
            print("发送", lis)
            
            while sock_count < len(lis):
                   sock_count += sock.sendto(lis[1].encode(), lis[0])
            #it must be a str
                        
def start_user(sock):
    pool = fu.ThreadPoolExecutor(PRO_MAX*2)
    for i in range(PRO_MAX):
        pool.submit(me.talk_to,args=(sock,))
    me.talk_to(sock)    

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(("127.0.0.1", 1234))
me=message()
usr=users()
for i in range(PRO_MAX):   
    pro=mut.Process(target=start_user,args=(sock,))
    pro.start()
start_user(sock)
#any pro can open sline,and call talk_to, and any sline can call talk_to

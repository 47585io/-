import socket
from concurrent import futures as fu
import multiprocessing as mut
import atexit
PRO_MAX = 1
# 设置要为每个port init's process and sline count
Mess_Buffer=1024
# 设置每一个sock cache buffer size

def whenexit():
    '''when exit, close all sock'''
    for mess in messes:
        mess.sock.close()
atexit.register(whenexit)


class users:
    # def talk_to(self,*arg):
    # print("my_usr")
    def __init__(self):
        self.users = {}
        #{name:(addr,port)}
        self.users_friend = {}
        #{name:[friendname]}
        self.now_in = []
        #[(now_in name)]
    def value_to_key(self,addrtup):
        for key,value in self.users.items():
            if value == addrtup:
                return key
        
    def Login(self, tup):
        name = tup[0].decode()
        name=name[6::]
        if name == '':
            return ''
        if name not in self.users:
            self.users[name] = tup[1]
        if name not in self.users_friend:
            self.users_friend[name] = []
        if name not in self.now_in:
            self.now_in.append(name)
        name += str(self.users_friend[name])
        print("end")
        return name

    def Isin(self, spilt_tup):
        if spilt_tup[1] in self.now_in:
            return 1
        return 0

    def addfriend(self, tup):
        addr = str(self.users.keys())
        return addr

USERS = users()
#The server save users obj


class message:
    '''read and process and send user send's mess'''
    def __init__(self, socktype="UDP", tup=("127.0.0.1", 1234)) -> None:
        if socktype == "TCP":
            self.sock = socket.socket()
        elif socktype == "UDP":
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.sock.bind(tup)
        if socktype == "TCP":
            self.sock.listen(20)
            self.sock.accept()
    #if it's TCP, listen and accept
    def bbmess(self,tmp):
        lis=[]
        if tmp[0].decode().startswith("LOGIN"):
            s=USERS.Login(tmp)
            print(USERS.value_to_key(tmp[1]))
            print(s)
            lis.append(s)
            lis.append(tmp[1])
            return 
    #default, the mess is name@str, read it , get the str and name, send str to name, and add mess from(my_name@str)
    #so send my_name@str to name's addr
        else:
            s_str,name=Spilt_Mess.Read_spilt(tmp[0])
            lis.append(Spilt_Mess.Send_spilt(s_str, USERS.value_to_key(tmp[1])))#from USERS get my_name  
            lis.append(USERS.users[name]) 
            return lis
    
    def talk_to(self, *arg):
        '''the talk_to going to recv a bytes mess from a user, after process, it send a bytes mess to other user'''
        while True:       
            sock_count = 0
            tmp = self.sock.recvfrom(Mess_Buffer)
            print("接收", tmp[0])
            lis = self.bbmess(tmp)
            print("发送", lis)
            while sock_count < len(lis[0]):
                sock_count += self.sock.sendto(lis[0], lis[1])
            #the lis[0] is from@str, lis[1] is send to addr
            #the lis can't use, it must use with USERS
            

class Spilt_Mess:
    '''The class have many process mess's func'''
    @staticmethod
    def Send_spilt(s_str, name):
        '''when read a defalut mess ,use it'''
        if name:
            return (name+"@"+s_str).encode()
        else:
            return s_str.encode()

    @staticmethod
    def Read_spilt(s_str):
        '''when read a defalut mess ,use it'''
        if s_str:
            s_str = s_str.decode()
            index = s_str.find(('@'))
            read_str = s_str[index+1::]
            name = s_str[0:index:]
            return (read_str, name)


def start(mess, arg):
    pool = fu.ThreadPoolExecutor(PRO_MAX)
    for i in range(PRO_MAX):
        pool.submit(mess.talk_to, arg)
    mess.talk_to(arg)


def main(messes, arg):
    usr = users()
    for mess in messes:
        pro = mut.Process(target=start, args=(mess, arg))
        pro.start()
    start(messes[0], arg)

# any pro can open sline,and call talk_to, and any sline can call talk_to
# defalut,the one port olny init one
# but, you can append it's pointer many count in messes list, you can init many frequency
# like below

messes = [message(), ]
messes.append(messes[0])
# messes.append(users())
main(messes, 0)

# and, the list's element type can no message, but it must have func talk_to
# and, I have prepared a parameter interface for you

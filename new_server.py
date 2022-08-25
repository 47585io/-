import multiprocessing
import socket
from concurrent import futures as fu
import multiprocessing as mut
from multiprocessing import Manager
import atexit
PRO_MAX = 1
# 设置要为每个server init's process and sline count
Mess_Buffer = 1024
# 设置每一个sock cache buffer size


class users:
    '''have all date for all process'''
    def __init__(self):
        self.users=mut.Queue()
        self.users.put({})
        #olny have  a dict, {name:(addr,port)}  #save all user, and their addr,port tup
        self.friend_list=mut.Queue()
        self.friend_list.put({})
        #olny hava a dict, {name[friendname]}  #save user name , and he has friend name list
        self.now_in=mut.Queue()
        self.now_in.put([])
        #olny have a list, [name] #save now_in user
        self.cache=mut.Queue()
        self.cache.put({})
        #olny hava a dict, {name:[cachemess]} #sava can't send str, wait that user login, send all to
    def search(self,going_search_queue,new_tup):
        '''going to old going_search_queue pointer's obj search to new_tup'''
        tmp=going_search_queue.get()
        if type(tmp) == list:
            tmp.append(new_tup)
            going_search_queue.put(tmp)
        else:
            tmp[new_tup[0]]=new_tup[1]
            going_search_queue.put(tmp)
    def getto(self,going_get_queue,index):
        '''get queue's dict or list index element'''
        tmp=going_get_queue.get()
        going_get_queue.put(tmp)
        return tmp[index]
    def add(self,tup):
        '''when a new user, call it'''
        self.search(self.users,tup)
        self.search(self.friend_list,(tup[0],[]))
        self.search(self.now_in,tup[0])
        self.search(self.cache,(tup[0],[]))
        tmo=self.now_in.get()
        self.now_in.put(tmo)
        print(tmo)
    def Login(self,tup):
        '''when user login, call it'''
        name = tup[0].decode()
        name = name[6::]
        if name == '':
            return ''
        self.add((name,tup[1]))
        friend=self.getto(self.friend_list,name)
        return name+str(friend)
    def addfriend(self):
        pass
        
class message:
    '''read and process and send user send's mess'''
#you can put a dict in queue
#the mess server can on many port, but it olny init users once, so their share queue
#a port can have many process, the same port's process will fight date, but their space is different, so if once process get user name and addr,must save in queue

    def __init__(self, tup=("127.0.0.1", 1234)) -> None:
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.sock.bind(tup)
        self.USERS = users()
    def bbmess(self, tmp):
        '''process going to send str'''
        lis = []
    #if user login, call login get username and friend_list, the end , send mess to src user
        if tmp[0].decode().startswith("LOGIN"):
            s = self.USERS.Login(tmp)
            lis.extend((s.encode(), tmp[1]))
            return lis
        #login return a decode str,OK,then let us to encode
        
    #default, the mess is name@str, read it , get the str and name, send str to name, and add mess from(my_name@str)
    #so send my_name@str to name's addr
        else:
            pass

    def talk_to(self, *arg):
        '''the talk_to going to recv a bytes mess from a user, after process, it send a bytes mess to other user'''
        while True:
            tmp = self.sock.recvfrom(Mess_Buffer)
            print("接收", tmp)
            lis = self.bbmess(tmp)
            print("发送", lis)
            self.Send(lis)

    def Send(self, lis):
        '''send bytes, can redefine in sonclass'''
        self.sock.sendto(lis[0], lis[1])
            #the lis[0] is from@str, lis[1] is send to addr


class Group_Mess(message):
    '''the group also is a user, but it's in server, and it's port in 1236, it's friend is all user in group, it's now_in user is now_in group's user'''

    def __init__(self, messaddr=("127.0.0.1", 1236)) -> None:
        message.__init__(self, messaddr)

    def talk_to(self, *arg):
      '''read bytes from a user, and send all user in the group'''
      while 1:
        str, addr = self.sock.recvfrom(Mess_Buffer)
        self.sock.sendto(str, addr)
    #diffrent port's date, it olny is  this port , all port's  process olny get itself port's date


class Spilt_Mess:
    '''The class have many process mess's func'''
    @staticmethod
    def Send_spilt(s_str, name):
        '''when send a defalut mess ,use it'''
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
        pool.submit(mess.talk_to, args=(arg,))
    mess.talk_to(arg)
#all sline share date on the process


def main(messes, arg):
    for mess in messes:
        pro = mut.Process(target=start, args=(mess, arg))
        pro.start()
    start(messes[0], arg)

#The one process olny have a mess server, a mess server can in diffrent port or process, but their share queue
# any pro can open sline,and call talk_to, and any sline can call talk_to
# defalut,the one port olny init one
# but, you can append it's pointer many count in messes list, you can init many frequency
# like below

messes = [message(), Group_Mess()]
messes.append(messes[0])
# messes.append(users())

def whenexit():
    '''when exit, close all sock'''
    global messes
    for mess in messes:
        mess.sock.close()
atexit.register(whenexit)

main(messes, 0)

# and, the list's element type can no message, but it must have func talk_to
# and, I have prepared a parameter interface for you

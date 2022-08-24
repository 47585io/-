import socket
from concurrent import futures as fu
import multiprocessing as mut
import atexit
PRO_MAX = 1
# 设置要为每个port init's process and sline count
Mess_Buffer = 1024
# 设置每一个sock cache buffer size


def whenexit():
    '''when exit, close all sock'''
    for mess in messes:
        mess.sock.close()


atexit.register(whenexit)


class users:
    # def talk_to(self,*arg):
    # print("my_usr")
    #def __init__(self):
    users = {}
        #{name:(addr,port)}
    users_friend = {}
        #{name:[friendname]}
    now_in = []
        #[(now_in name)]

    def value_to_key(self, addrtup):
        '''through the value cheak key in list'''
        for key, value in self.users.items():
            if value == addrtup:
                return key

    def Login(self, tup):
        '''when user login, call it, it going to init user's date'''
        name = tup[0].decode()
        name = name[6::]
        if name == '':
            return ''
    #get name and judge, if user not in list, add user in list
        if name not in self.users:
            self.users[name] = tup[1]
        if name not in self.users_friend:
            self.users_friend[name] = []
        if name not in self.now_in:
            self.now_in.append(name)
        name += str(self.users_friend[name])
        return name
    #return username and user friend_list

    def Isin(self, spilt_tup):
        '''user whether now'''
        if spilt_tup[1] in self.now_in:
            return 1
        return 0

    def getfriend(self, tup):
        '''get all user name list'''
        addr = str(self.users.keys())
        return addr


USERS = users()
#The server save users obj once


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
        self.cache_buffer = {}
    #sava can't send str, wait that user login, send all to

    def bbmess(self, tmp):
        '''process going to send str'''
        lis = []
    #if user login, call login get username and friend_list, the end , send mess to src user
        if tmp[0].decode().startswith("LOGIN"):
            s = USERS.Login(tmp)
            lis.extend((s.encode(), tmp[1]))
            return lis
        #login return a decode str,OK,then let us to encode

    #default, the mess is name@str, read it , get the str and name, send str to name, and add mess from(my_name@str)
    #so send my_name@str to name's addr
        else:
            s_str, name = Spilt_Mess.Read_spilt(tmp[0])
            # from USERS get my_name
            lis.append(Spilt_Mess.Send_spilt(
                s_str, USERS.value_to_key(tmp[1])))
            lis.append(USERS.users[name])
            return lis

    def talk_to(self, *arg):
        '''the talk_to going to recv a bytes mess from a user, after process, it send a bytes mess to other user'''
        while True:
            tmp = self.sock.recvfrom(Mess_Buffer)
            print("接收", tmp)
            lis = self.bbmess(tmp)
            print("发送", lis)
            self.Send(lis)

    def Send(self, lis):
        sock_count = 0
        while sock_count < len(lis[0]):
            sock_count += self.sock.sendto(lis[0], lis[1])
            #the lis[0] is from@str, lis[1] is send to addr


class Group_Mess(message):
    '''the group also is a user, but it's in server, and it's port in 1236, it's friend is all user in group, it's now_in user is now_in group's user'''

    def __init__(self, messtype="UDP", messaddr=("127.0.0.1", 1236)) -> None:
        message.__init__(self)


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
        pool.submit(mess.talk_to, args=(arg,))
    mess.talk_to(arg)

def main(messes, arg):
    usr = users()
    for mess in messes:
        pro = mut.Process(target=start, args=(mess, arg,))
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

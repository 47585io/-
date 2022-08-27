from email.mime import message
from os import POSIX_FADV_RANDOM
import socket
import threading as th
import atexit
Mess_Buffer = 1024
Max_Mess=10
#recv buffer size and user mess_list size
UDP_SOCK = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
UDP_SOCK.bind(("",0))
TCP_SOCK=socket.socket()
TCP_SOCK.bind(("", 0))
#the user, olny two port with server
USER_NAME=""
Mess_Friend={}
#记录与好友通信mess
Talk_With=[]
#who talk with now

def whenexit():
    '''when exit, close all sock'''
    UDP_SOCK.close()
    TCP_SOCK.close()
atexit.register(whenexit)

class UDP_Mess:
    def __init__(self) -> None:
        self.MessCache = []
        self.index = -1   
        self.yes=0    
#Mess list: Max 10 mess,  index: now new mess index, yes:the mess yes or on new mess
    def get(self):
        '''the func pop a element from MessCache head, and return'''
        if self.index>=0:
            mess = self.MessCache[0]
            del self.MessCache[0]
            self.index-=1
            return mess
    def getnew(self):
        '''get best new mess'''
        while self.yes==0:
            pass
        self.yes= 0
        tmp=self.MessCache[self.index]
        del self.MessCache[self.index]
        self.index-=1
        return tmp
    
    def Send(self, sock,send_str, to_user=None):
        '''when want send mess to server, call it'''
        self.yes=0
        #when send a mess, Read going to read a new mess, the yes=0, now index is no new
        send_str = Spilt_Mess.Send_spilt(send_str,to_user)
        sock.sendto(send_str, ("127.0.0.1", 1234))

    def Read(self,sock):
        '''every once, recv a mess and add it to MessCache, if mess count >Max, del old mess'''
        while 1:
            tmp = sock.recvfrom(1024)
            self.MessCache.append(tmp[0])
            self.index += 1
            self.yes=1
            if(self.index >= Max_Mess):
                del self.MessCache[0]
                self.index -= 1
            print(tmp[0])
            
UDP = UDP_Mess()


class friends:
    def __init__(self) -> None:
        self.friend_list = []

    def show(self):
        return self.friend_list
    
    def format_list(self,list_):
        new_list=[]
        for name in list_:
            if name not in self.friend_list and name != USER_NAME:
                new_list.append(name)
        return new_list
    
    def from_server_get_friend_list(self,name):
        UDP.Send("AddFriend "+name)
        s_str=UDP.getnew()
        return Spilt_Mess.Friend_list_Read_Spilt(s_str)
             
    def addfriend(self):
        list_=self.from_server_get_friend_list()
        list_=self.format_list(list_)
        self.friend_list.extend(list_)
        
class Spilt_Mess:
    @staticmethod
    def Send_spilt(s_str,name):
        if name:
            return (name+"@"+s_str).encode()
        else:
            return s_str.encode()
    @staticmethod
    def Read_spilt(s_str):
      if s_str:
        s_str=s_str.decode()
        index=s_str.find(('@'))
        read_str = s_str[index+1::]
        name = s_str[0:index:]
        return (read_str, name)
    
    @staticmethod
    def Friend_list_Send_Spilt(friend_list):
        s_str=""
        for friend in friend_list:
            s_str+="'"
            s_str+=friend
            s_str+="'"
        return s_str.encode()
    
    @staticmethod
    def Friend_list_Read_Spilt(s_str):
        s_str=s_str.decode()
        friend_list=[]
        start,end,index=(0,0,0)
        while index<len(s_str):
            if s_str[index]=="'":
                start=index+1
                index+=1
                while True:
                    if s_str[index] == "'":
                        end=index
                        friend_list.append(s_str[start:end:])
                        break
                    index+=1
            index+=1
        return friend_list
        

def main(mess,sock):
    r=th.Thread(target=mess.Read,args=(sock,))
    r.start()
    #GNU_R=th.Thread(target=gra.show())
    while True:
        s=input()
        mess.Send(sock,s)
        if s =="AddFriend":
            print(Spilt_Mess.Friend_list_Read_Spilt(mess.get()))
 
main(UDP,UDP_SOCK)
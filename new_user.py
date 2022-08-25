import socket
import threading as th
import atexit
Mess_Buffer = 1024
UDP_SOCK = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
UDP_SOCK.bind(("",0))
TCP_SOCK=socket.socket()
TCP_SOCK.bind(("", 0))
#the user, olny two port with server
USER_NAME=""

def whenexit():
    '''when exit, close all sock'''
    UDP_SOCK.close()
    TCP_SOCK.close()
atexit.register(whenexit)


class UDP_Mess:
    def __init__(self) -> None:
        self.MessCache = []
        self.index = -1
        self.NewMess = ""
        self.yes = 0

    def get(self):
        while 1:
            if self.yes:
                self.NewMess = self.MessCache[self.index]
                del self.MessCache[self.index]
                self.index -= 1
                self.yes = 0
                return self.NewMess

    def Send(self, sock,send_str, to_user=None):
        send_str = Spilt_Mess.Send_spilt(send_str,to_user)
        sock.sendto(send_str, ("127.0.0.1", 1234))

    def Read(self,sock):
        while 1:
            tmp = sock.recvfrom(1024)
            self.MessCache.append(Spilt_Mess.Read_spilt(tmp[0]))
            self.yes = 1
            self.index += 1
            if(self.index >= 10):
                del self.MessCache[0]
                self.index -= 1
            print(tmp[0])

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


def main(mess,sock):
    r=th.Thread(target=mess.Read,args=(sock,))
    r.start()
    while True:
        s=input()
        mess.Send(sock,s)
 
UDP=UDP_Mess()   
main(UDP,UDP_SOCK)
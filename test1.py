import socket
import os


def Get_mess_spilt(fromwho, to, filename):
    return ("Get From " + fromwho + " To " + to + " " + filename).encode()


try:
    sock = socket.socket()
    sock.connect(("127.0.0.1", 1237))
    #size = os.path.getsize("furry.gif")
    file = open("1.gif", "wb")
    sock.send(Get_mess_spilt("me", "he", "furry.gif"))
    sock.recv(1024)
    while 1:
        #date = file.read(1024)
        tmp =sock.recv(1024)
        file.write(tmp)
        #size -= 1024
except Exception:
    print("error")
    file.close()
    sock.close()
else:
    file.close()
    sock.close()

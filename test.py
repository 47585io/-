import socket
import os


def Send_mess_spilt(fromwho, to, filename):
    return ("Send From " + fromwho + " To " + to + " " + filename).encode()


try:
    sock = socket.socket()
    sock.connect(("127.0.0.1", 1237))
    size = os.path.getsize("furry.gif")
    file = open("furry.gif", "rb")
    sock.send(Send_mess_spilt("me","he","furry.gif"))
    sock.recv(1024)
    while size > 0: 
        date = file.read(1024)
        sock.send(date)
        size -= 1024
except Exception:
    file.close()
    sock.close()
else:
    file.close()
    sock.close()


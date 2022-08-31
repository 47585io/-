import socket
import os

sock=socket.socket()
sock.bind(("127.0.0.1",1238))
sock.listen(20)
new_sock,addr=sock.accept()
file=open("1.gif","wb",)
while 1:
        date = new_sock.recv(1024)
        file.write(date)
file.close()
new_sock.close()
sock.close()
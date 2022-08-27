
import gevent
import time

def fun1(h):
    for i in range(5):
        print(i)
        #gevent.sleep(1)
        print(h," ",i)
        time.sleep(1)



gevent.joinall([
    gevent.spawn(fun1, "work1"),
    gevent.spawn(fun1, "work2")
])

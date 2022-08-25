'''import multiprocessing as mut
class one:
    def __init__(self):
     self.q=mut.Queue()
     self.q.put("4")
     self.q.put("&")
     self.q.put("&")
    def h(self):
        print(self.q.get())
class two:
    e=one()
    print("init")
    def f(self):
        self.e.h()
        
for i in range(3):
    ser=two()
    pro=mut.Process(target=ser.f)
    pro.start()'''


print(type([]))
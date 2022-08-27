def a():
    print("a")
    
def b():
    print("b")



class h:
    def Isin(self, win):
        global my_name
        win.resizable(0, 0)
        file = open("/home/tom/vscode/idea/socqq/name.txt", "r+")
        my_name = file.read()
        My_Mess.Send(str("LOGIN "+my_name))
        tmp = My_Mess.get()

        if tmp == "":
            self.new_page(win, file)
            print("new")
        else:
            self.back_page(win)
            print("old")
            sleep(2)
            self.friend_list(win)

            file.close()
            self.lab.place_forget()
        #self.friend_list()

    def back_page(self,win):
        win.geometry("210x240")
        win.title("欢迎!")
        win.config(background="#C0D9D9")
        
        self.pic = tk.PhotoImage(file="/home/tom/vscode/github/--1/furry.gif")
        self.lab=tk.Label(win,text="hh",image=self.pic,activebackground='green',width=80,height=60)
        self.lab.place(x=65,y=10) 
        
    def new_page(self,win,file):
        self.back_page(win)  
        f1=tk.LabelFrame(win)
        f1.place(x=20,y=110)
        ent = tk.Entry(f1, text="a new name",selectbackground="green",width=13)
        ent.pack()  
        
        def login():
            My_Mess.Send("LOGIN "+ent.get())
            buf = ent.get()
            file.write(buf)
            file.close()
            f1.place_forget()
            ent.pack_forget()
            but.place_forget()
            self.friend_list(win)
            
        but = tk.Button(text="login", command=login,background="#C0D9D9",activebackground='pink')
        but.place(x=150,y=200)  
#get and write to file

    def addfriend(self,win):
        my.addfriend()
        self.list=tk.Listbox(win)
        self.list.place(x=0,y=0)
        i=0
        for name in my.friend_list.keys():
            self.list.insert(i,name)
            i+=1
        def Return():
            self.list.place_forget()
            self.but.place_forget()    
            
        self.but =tk.Button(text="return",command=Return)
        self.but.place(x=130,y=200)
        def Ok():
            self.labgroup=[]
            
            self.labgroup.append
        self.enter=tk.Button(text="Ok",command=Ok)
            
    def friend_list(self,win):
        fa1 = tk.Frame(win, background='#C0D9B9',width=210,height=50)
        fa1.pack()
        lab=tk.Label(fa1,image=self.pic,
                            activebackground='green',width=60,)
        lab.place(x=0,y=0)
        lab2=tk.Label(fa1,font=(0,8),text=my_name+"localhost"+" "+str(my_port),background='#C0D9B9',width=12,anchor="nw",)
        lab2.place(x=60,y=0)
        if not my.friend_list:
            lab3=tk.Label(text="No Friends!",font=(30),background="#C0D9D9",height=4)
            lab3.pack()
        
        menu = tk.Menu(win, tearoff=False, activebackground="pink")
        menu.add_command(label="add",command=lambda :self.addfriend(win))
        def pos(enxy):
            menu.post(enxy.x_root, enxy.y_root)
        win.bind("<Button-3>", pos)
        
        win.update()
        #self.talk_with()
        input("按任意键开始")
        s=thr.Thread(target=self.talk_with)
        s.start()

import tkinter as tk

win=tk.Tk()
win.config(background="#282c34")
win.mainloop()
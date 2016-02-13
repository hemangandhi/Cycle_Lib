

class Loop():
    def __init__(self):
        self.l=[]
    def push(self,item):
        self.l.append(item)
    def pop(self,item):
        assert(len(self)>0)
        return self.pop(0)
    def __len__(self):
        return len(self.l)
    def __getitem__(self,key):
        return self.l[key%len(self)]

"""

    def is_valid_solution(self):
        for index in range(len(self.l)):
            if 


"""

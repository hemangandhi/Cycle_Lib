from Classes import *

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


    def is_valid_solution(self):

        return all([ self.l[index].satisfies(self.l[index+1]) for index in range(len(self.l))])


"""
Given pairs as a set of all pairs 

{p1, p2, ..., pn} s.t. pi is unique

same for repeats, a set {}

"""





############ Part I: for given start, find all loops

def loop_for(first, pairs):
    return loop_starts_with(first, set(),first, pairs) 
    
#should pass to loop_starts_with WITHOUT the pair that is first in pairs
#start with current

def loop_start_with(first, repeats, current, pairs):
    #add asserts
    #current is the one we are going from to try and reach the first

    ret = []
    non_repeat_pairs = pairs - repeats
    valid_conts = filter(current.satisfies, non_repeat_pairs)

    possibilities = []
    for cont in valid_conts:
        if cont.get_want() == first.get_have():
            ret.append([cont])
        else:
            possibilities += cont        

    path_from_pos = loop_starts_with(first, repeats + set(cont), cont, pairs)


    for sol in path_from_pos:
        ret.append([first]+ sol)

    return ret


    
            
    

######### Part II:  













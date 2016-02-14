import random
import string
import itertools
from Classes import *


"""
Given pairs as a set of all pairs 

{p1, p2, ..., pn} s.t. pi is unique

same for repeats, a set {}

"""

###### All valid loops for set of pairs

def to_graph(p):
    #p is a list of pairs

    return { i:set(filter(i.satisfies, p)) for i in p}

def connected(node, graph, acc, d):
    nbhs = graph[node]
    graph[node] = {}
    for i in nbhs:
        for j in range(len(acc)):
            if acc[j] == i:
                if i in d: d[i].add(acc[j+1:])
                else: d[i] = set([acc[j+1:]])
                break
        else:
            connected(i, graph, acc+(i,), d)
    
                   
        


def all_cycles(graph):
    d = {}
    for node in graph:
        if node not in d:
            connected(node, graph.copy(),(node,), d)

    return d

def sol_dict(p):
    return all_cycles(to_graph(p))
    
   
    





    

    
    







#####Testing===========================================

def rand_name(n):
    return ''.join(random.choice(string.ascii_uppercase) for i in range(n))


def rand_waitlist():
    return random.randint(0,2)

def int_id():
    i = 1
    while True:
        yield i
        i += 1

def gen_n_pairs_j_classes(n, j):
    #n pairs and j different classes in the college
    ret = set()
    classes = [rand_name(1) for i in range(j)]  #1 letter class names
    g = int_id()
    while len(ret) < n:
        a= rand_waitlist()
        b = rand_waitlist()
        c1 = random.choice(classes)
        c2 = random.choice(classes)
        if c1!=c2 or b < a:
            ret.add(      Pair(   Have(c1, a), Want(c2,b, ), next(g)       ))

    return ret

def n_j(n,j):
    return gen_n_pairs_j_classes(n, j)
     
def is_valid_sol(lis):
    boo = all([ lis[index].satisfies(lis[index+1]) for index in range(len(lis)-1)])
    return lis[-1].satisfies(lis[0]) and boo

def test_pset(pset):
    sol_set = sol_dict(pset)
    print(sol_set)
    
    #for sol in sol_set:
        #print(sol, is_valid_sol(sol))

def test(n,j):
    pairs = gen_n_pairs_j_classes(n, j)
    first = pairs.pop()
    pairs.add(first)
    sol_set = loop_for(first, pairs)
    print(sol_set)
    return None
    
    for sol in sol_set:
        print(sol, is_valid_sol(sol))

    return all([is_valid_sol(i) for i in sol_set])
            
        
def ps(s):
    for i in s: print(i)

def test_t_times(t,n,j): #n pairs for j classes
    for i in range(t):
        print("--------------------------------------------------")
        a = n_j(n,j)
        ps(a)
        print("Solution set:\n")
        test_pset(a)


















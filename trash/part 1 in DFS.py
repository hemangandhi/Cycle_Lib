
def filt(f, lis):
    ret = []
    for i in lis:
        if f(i): ret.append(i)
    return ret

def loop_for(first, pairs):
    return all_loops(first,pairs)
    
#should pass to loop_starts_with WITHOUT the pair that is first in pairs
#start with current

def loop_start_with(first, repeats, current, pairs):
    #add asserts
    #current is the one we are going from to try and reach the first

    ret = []
    non_repeat_pairs = pairs - repeats
    valid_conts = filt(current.satisfies, non_repeat_pairs)

    possibilities = []
    for cont in valid_conts:
        if cont.satisfies(first):
            ret.append([current, cont])

        possibilities += [cont]
            
    
    path_from_pos = []
    for pos in possibilities:
        repeats.add(pos)
        path_from_pos.append(loop_start_with(first, repeats, pos, pairs))
        repeats.remove(pos)
    path_from_pos = sum(path_from_pos, [])    



    for sol in path_from_pos:
        ret.append([current]+ sol)

    return ret



def all_loops(start, pairs):
    continuations = []
    for p in pairs:
        if start.satisfies(p): continuations.append(p)
    all_lines_from_c = []
    for c in continuations:
        lines_per_c = LimLine(c, start, [], pairs)# this is a list of lists
        if lines_per_c == -1: continue
        for l in lines_per_c:
            if l != -1:
                all_lines_from_c += l
    for to_a in all_lines_from_c:
        to_a.insert(start,0)
    return all_lines_from_c



def LimLine(c, A, lim, pairs): # returns list of lists
    if c is A:
        return [[A]]
    acc = []
    for ngh in pairs:
        if c.satisfies(ngh) and ngh not in lim:
            v = LimLine(ngh, A, lim+[c], pairs)
            if v!= -1:
                for line in v:
                    acc.append([c]+line)

    if acc==[]:return -1
    else: return acc
            

def to_graph(p):
    #p is a list of pairs

    return { i:set(filter(i.satisfies, p)) for i in p}


def connected(node, graph, acc, d):
    nbhs = graph[node]
    graph[node] = {}
    for i in nbhs:
        for j in range(len(acc)):
            if acc[j] == i:
#               d.add(acc[j:])
                
                if i in d: d[i].add(acc[j+1:])
                else: d[i] = set([acc[j+1:]])
                break
        else:
            connected(i, graph, acc+(i,), d)
    
                   

def all_cycles(graph):
    d = Loop()
    #d = dict {}
    for node in graph:
        if node not in d:
            connected(node, graph.copy(),(node,), d)

    return d

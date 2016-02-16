#! /usr/bin/env python3
import json
import Classes as cls
import subsets
from functools import reduce

"""
{'sets':[{'set':[Pair,...], 'votes': [uid,...]},...]}
"""

def counter(by = 1):
  i = 0
  while True:
    yield i
    i += by

class PairEncoder(json.JSONEncoder):
  def default(self, obj):
    if isinstance(obj, cls.Desire):
      h = isinstance(obj, cls.Have)
      return {'__Desire__':h, 'name': obj.name, 'position':str(obj.position)}
    elif isinstance(obj, cls.Pair):
      return {'__Pair__':True, 'have': obj.have, 'want':obj.want, 'id': str(obj.id)}
    else:
      return json.JSONEncoder.default(self, obj)

def as_class(dct):
  if '__Desire__' in dct:
    if dct['__Desire__']:
      return cls.Have(dct['name'], int(dct['position']))
    else:
      return cls.Want(dct['name'], int(dct['position']))
  elif '__Pair__' in dct:
    return cls.Pair(dct['have'], dct['want'], int(dct['id']))
  else:
    return dct

def read_db():
  with open('db/db.json') as f:
    return json.load(f, object_hook=as_class)

def dump_db(db):
  with open('db/db.json', 'w') as f:
    json.dump({'sets': db}, f, cls=PairEncoder)

def usr_sets(uid):
  sets = read_db()['sets']
  return [i for i in sets for j in i['set'] if isinstance(j, cls.Pair) and j.id == uid]

def enc_one_pair(pair):
  return json.dumps(pair, cls=PairEncoder)

def load_vote(st):
  dct = json.loads(st, object_hook=as_class)
  db = read_db()
  for i in db['sets']:
    if i['set'] == dct['idx']:
      if int(dct['uid']) not in i['votes']:
        i['votes'].append(dct['uid'])
        dump_db(db['sets'])
        return len(i['votes'])
  return -1

def update_pairs(new):
  new = json.loads(new, object_hook=as_class)
  db = read_db()['sets']
  snd = reduce(set.__or__, map(lambda x: set(x['set']), db))
  snd |= set(new)
  
  with open('db/udb.json') as f:
    snd |= set(json.load(f, object_hook=as_class)['pairs'])

  cyl = subsets.all_cycles(subsets.to_graph(snd))
  n_db = []
  for i in cyl:
    for k in i:
      if k in snd:
        snd.remove(k)
    for j in db:
      if j['set'] == i:
        n_db.append(j)
        break
    else:
      n_db.append({'set': list(i), 'votes':[]})
  
  with open('db/udb.json', 'w') as f:
    json.dump({'pairs': list(snd)}, f, cls=PairEncoder)
  dump_db(n_db)
  return True

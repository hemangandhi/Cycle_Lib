#! /usr/bin/env python3

import http.server as srv
import cgi
import os
import srv_util as util
from oauth2client import client, crypt

CLIENT_ID = "175322907066-1g3k9vba0jji3qi3kf401lg5t8fhk15n.apps.googleusercontent.com"
users = set()

def verify_token(token):
  try:
    idinfo = client.verify_id_token(token, CLIENT_ID)
    if idinfo['aud'] != CLIENT_ID: #WEB_CLIENT_ID is CLIENT_ID...
      raise crypt.AppIdentityError("Unrecognized client.")
    if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
      raise crypt.AppIdentityError("Wrong issuer.")
#    if idinfo['hd'] != APPS_DOMAIN_NAME: #maybe when we're legit...
#      raise crypt.AppIdentityError("Wrong hosted domain.")
  except crypt.AppIdentityError as e:
    print("oauth2client error:",e)
    return False
  else:
    return idinfo['sub']

def load_pg(loc, isFile=True):
  if isFile:
    with open(loc) as f:
      return load_pg(f.read(), False)
  with open('web/html/top_frame.html') as t:
      return loc.format(gTop = t.read())

def gen_usr_pg(uid):
  if uid not in users:
    return load_pg("<html><head>{gTop}<h1>Sign in!</h1></body></html>", False)
  
  with open('web/html/usr_template.html') as f:
    lInf = util.usr_sets(uid)
    dInf = dict()
    for i in lInf:
      for j in range(len(i['set'])):
        if i['set'][j].id != uid:
          continue
        elif i['set'][j] in dInf:
          dInf[i['set'][j]].append({'set': i['set'][j:] + i['set'][:j], 'votes':i['votes']})
        else:
          dInf[i['set'][j]] = [{'set': i['set'][j:] + i['set'][:j], 'votes':i['votes']}]

    uInf = ""
    uObj = "<script> var db = {{"
    ctr = util.counter()
    for i in dInf:
      v = {'haveName': i.have.name, 'havePos': i.have.position, 'wantName': i.want.name, 'wantPos': i.want.position}
      uInf += "<h3>Giving up {haveName} at {havePos} for {wantName} at {wantPos}.</h3>\n".format(**v)
      uInf += "<div>This can be done by:\n<ul>"    

      for j in dInf[i]:
        ct = next(ctr)
        iv = uid in j['votes']
        if not iv:
          cl = "vote"
        else:
          cl= "noVote"
        uInf += "<li id=\"{ct}\" class=\"{cl}\"> You giving up {c}<br/>".format(c=j['set'][0].have.name, cl=cl, ct=ct)

        for k in j['set'][1:]:
          uInf += " the person getting that giving up {g} and <br/>".format(g=k.have.name)
        uInf += " you getting {w}.".format(w=j['set'][0].want.name)

        if iv:
          if len(set(map(lambda x: x.id, j['set']))) == len(j['votes']):
            eve = " In fact, everybody has voted for it!"
          else:
            eve = ""
          uInf += "<strong>You've voted for this!{eve}</strong></li>".format(eve=eve)
        else:
          uInf += "<span id=\"{ct}\">This has {v} votes.</span></li>".format(v=len(j['votes']),ct=str(ct)+'votes')
        uObj += "'{ct}': {enc},".format(ct=ct, enc=util.enc_one_pair(j['set']).replace('{','{{').replace('}','}}'))

      uInf += "</div>"
      uObj += "}};</script>"
    uInf += "WARNING: Unfilled requests are not displayed!"
    return load_pg(f.read().format(uinfo = uInf, uobj = uObj), False)

class WaitListReq(srv.SimpleHTTPRequestHandler):
  def send_content(self, txt, c_type = 'text/plain'):
    self.send_header('content-type',c_type)
    self.send_header('content-length',str(len(txt)))
    self.end_headers()
    self.wfile.write(txt.encode('utf-8'))

  def do_POST(self):
    ctype, p = cgi.parse_header(self.headers['Content-Type'])
    if ctype == 'application/x-www-form-urlencoded' and self.path == "/tokensignin":
      l = int(self.headers['Content-Length'])
      postvars = cgi.parse_qs(self.rfile.read(l), keep_blank_values = 1)
      ver = verify_token(postvars[b'id_token'][0])
      if ver != False:
        users.add(int(ver))
        self.send_response(200)
        self.send_header("Access-Control-Allow-Origin",'*')
        self.send_content(ver)
      else:
        self.send_response(400)
        self.send_content("None")
    elif ctype == 'application/x-www-form-urlencoded' and self.path == "/tokensignout":
      l = int(self.headers['Content-Length'])
      postvars = cgi.parse_qs(self.rfile.read(l), keep_blank_values = 1)
      if postvars[b'id_token'][0] in users:
        users.remove(int(postvars[b'id_token'][0]))
      self.send_response(200)
      self.send_content("None")
    
    elif ctype == 'application/json' and self.path =='/vote':
      dt = self.rfile.read(int(self.headers['content-length'])).decode('utf-8')
      dt = util.load_vote(dt)
      if dt > 0:
        self.send_response(200)
      else:
        self.send_response(400)
      self.send_content(str(dt))
    elif ctype == 'application/json' and self.path == '/swap':
      dt = self.rfile.read(int(self.headers['content-length'])).decode('utf-8')
      dt = util.update_pairs(dt)
      if dt:
        self.send_response(200)
      else:
        self.send_response(400)
      self.send_content(str(dt))
    else:
      self.send_response(404)
      self.send_content(load_pg("<html><head>{gTop}<h1>Page not found!</h1></body></html>", False), "text/html")

  def do_GET(self):
    if self.path.startswith('/usr'):
      id = int(self.path[len('/usr'):])
      pg = gen_usr_pg(id)
      self.send_response(200)
      self.send_content(pg, "text/html")
      return

    if self.path in ['/', '']:
      self.path = '/index.html'
    
    if self.path.endswith('.html'):
      self.path = '/web/html' + self.path
    elif self.path.endswith('.js'):
      self.path = '/web/js' + self.path
    elif self.path.endswith('.css'):
      self.path = '/web/css' + self.path

    q = srv.SimpleHTTPRequestHandler.translate_path(self, self.path)
    if os.path.exists(q) and srv.SimpleHTTPRequestHandler.guess_type(self, self.path) == "text/html":
      self.send_response(200)
      self.send_content(load_pg(q), "text/html")
    else:
      srv.SimpleHTTPRequestHandler.do_GET(self)

    

if __name__ == "__main__":
  s = srv.HTTPServer(('', 8080), WaitListReq)
  s.serve_forever()

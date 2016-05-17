# SCHEMA

## db.json

This file stores all the cycles and who voted for them.

Format:

- Sets has an array of "cycle objects":
  - Votes is a list of the UIDs of voters.
  - set is a list of pairs that creates the cycle. (Inside this is an encoded pair object, see [../srv_util.py] for details.

## udb.json

This file stores all the desires not in a cycle.

Format:

- Pairs is an array of encoded pair objects see [../srv_util.py] for details.

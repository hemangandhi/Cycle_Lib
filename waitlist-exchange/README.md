# Cycle-lib

Find cycles in a graph!

## What?

This emulates exchanges for bartering trading circles. This library hopes to find such trading cycles which are modelled as cycles in a graph.

An exchange is a user's (or trader's) statement that they have an object and for it are willing to give up another object.

Exchanges are connected where only viable trades are maintained in the graph.

Trades can have 3 states:
* Found: where the trade has been found.
* Locked: where all members of the trade have committed to the trade and cannot partake in other trades.
* Deleted: where all exchanges are seen as "done" and removed from the graph

## As a REST API...

Any N/A would 404. An exchange is a node. A trade is a cycle.

|URL           |GET                                     |PUT       |POST                   |DELETE                              |
|--------------|----------------------------------------|----------|-----------------------|------------------------------------|
|/exchanges/:id     | All the exchanges for the user            | N/A | N/A | Remove user's nodes                        |
|/exchanges/:id/has/:tag| All the exchanges with user having a certain tag      | N/A      | N/A                   | Removes the exchanges with the tag |
|/exchanges/:id/wants/:tag| All the exchanges with user wanting a certain tag      | N/A      | N/A                   | Removes the exchanges with the tag |
|/exchanges/:id/:have/:want| The node with user having have and wanting want      | N/A      | N/A                   | Removes the exchange |
|/login        | Whether you're logged in (as an admin)       | N/A      | Log in                | N/A                                |
|/cycles/:id     | All the cycles for the user            | N/A | N/A | Remove user's nodes                        |
|/cycles/:id/has/:tag| All the cycles with user having a certain tag      | N/A      | N/A                   | Removes the cycles with the tag |
|/cycles/:id/wants/:tag| All the cycles with user wanting a certain tag      | N/A      | N/A                   | Removes the cycles with the tag |
|/node/:id| The node with the id. | N/A | N/A | Removes the node |
|/cycles/lock| All the locked cycles | N/A | Locks a list of nodes | N/A |
|/cycles/state/:id | The state of locked trade (defaults to deleted) | N/A | Updates the trade state | Deletes the trade |

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

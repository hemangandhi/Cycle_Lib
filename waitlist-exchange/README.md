# Cycle-lib

Find cycles in a graph!

## As a REST API...

Any N/A would 404.

|URL           |GET                                     |PUT       |POST                   |DELETE                              |
|--------------|----------------------------------------|----------|-----------------------|------------------------------------|
|/user/:id     | All the cycles for the user            | Add user | Add exchange for user | Remove user                        |
|/user/:id/:tag| All the cycles with a certain tag      | N/A      | N/A                   | Removes the exchanges with the tag |
|/login        | Whether you're logged in               | N/A      | Log in                | N/A                                |

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

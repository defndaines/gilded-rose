# Gilded Rose

A Clojure implementation of the Gilded Rose kata
([Original](https://iamnotmyself.com/2011/02/13/refactor-this-the-gilded-rose-kata/))
using [clara-rules](https://github.com/cerner/clara-rules).

I think I first came across Jim Weirich's
[version](https://github.com/jimweirich/gilded_rose_kata), and I've stolen test
ideas from there.


## The Kata

Transcribed in [KATA.md](KATA.md)


## Usage

This project is really just set up to be exercised by tests.
```
lein test gilded-rose.core-test

Ran 6 tests containing 18 assertions.
0 failures, 0 errors.
```

The tests invoke a session and create items to be aged with the following
pattern:
```clojure
(let [session (-> (rules/mk-session 'gilded-rose.core)
                  (rules/insert
                    (->Item "Doohickey" 3 5 :normal)
                    (->Item "Sulfuras" 0 50 :special)
                    (->Item "Backstage Pass to TANSTAAFL Concert" 3 5 :backstage-pass)
                    (->Item "Aged Brie" 1 10 :special))
                  (rules/fire-rules))
      results (rules/query session completed-items)]
  ,,,)
```


## License

Copyright © 2017 Michael S. Daines

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

# indicators-service

Provides a JSON based REST microservice providing search capabilities on an open source intelligence feed provided by AlienVault OTX.

## Requirements

A recent JVM (Java Virtual machine).
Clojure CLI https://clojure.org/guides/install_clojure
Git https://git-scm.com/book/en/v2/Getting-Started-Installing-Git

Install Polylith tool https://cljdoc.org/d/polylith/clj-poly/0.2.19/doc/install

## Running locally

Build an uberjar

```sh
$ ./build-uberjar.sh
```

Start a local server

```sh
$ ./start-dev-server.sh
```

Navigate to a page like http://localhost:8080/indicators?type=YARA


## Endpoints
returns a document by its ID.
GET /indicators/:id

Return all documents
GET /indicators

Return all documents by its type.
GET /indicators?type=IPv4

Valid values for `type` are one of
> URL hostname FileHash-SHA256 FileHash-SHA1 IPv4 CVE email YARA FileHash-MD5 domain


## Running tests

```sh
$ poly test
```


## Docker
TODO
Note: Build an uberjar first

docker build -t indicators-backend .
docker run -p 8080:8080 -it --rm indicators-backend


## Misc
poly check
poly info
poly libs


## Database

This project uses a custom in-memory database backed by a map.
It would make sense to use an existing solution (ie H2, SQLite, etc) instead, but for this project this was built.

Its structure is as follows:

```clojure
{:indicators {1 {:indicator "85.93.20.243"
                   :description ""
                   :created "2018-07-09T18:02:40"
                   :title ""
                   :content ""
                   :type "IPv4"
                   :author-name "scottlsattler"
                   :id 1}
                2 {:indicator "71.24.15.164"
                   :description ""
                   :created "2019-07-10T18:02:40"
                   :title ""
                   :content ""
                   :type "YARA"
                   :author-name "scottlsattler"
                   :id 2}
                3 {:indicator "91.92.33.129"
                   :description ""
                   :created "2020-07-11T18:02:40"
                   :title ""
                   :content ""
                   :type "IPv4"
                   :author-name "AlienVault"
                   :id 3}}
   ;; Map representing an inverted index of indicators by type
   :indicator-type->ids {"YARA" #{2}
                         "IPv4" #{1 3}}

   ;; Map representing an inverted index of indicators by author-name
   :indicator-author-name->ids {"scottlsattler" #{1 2}
                                "AlienVault" #{3}}}
```

The `:indicators` key stores a map that can be used to lookup an indicator by id.
`:indicator-type->ids` stores an inverted index allowing fast lookups of all indicators by type
`:indicator-author-name->ids` stores an inverted index allowing fast lookups of all indicators by author name

The indexes are stored as sets in the event a `POST /indicators/search` is implemented.
For example to get all indicators by `scottlsattler` and of type `YARA` we could fetch the sets and use a `clojure.set/intersection` to find them.
Or if we wanted to find all indicators created by `scottlsattler` or `AlienVault` we could use a `clojure.set/union`


<img src="logo.png" width="30%" alt="Polylith" id="logo">

The Polylith documentation can be found here:

- The [high-level documentation](https://polylith.gitbook.io/polylith)
- The [poly tool documentation](https://cljdoc.org/d/polylith/clj-poly/CURRENT)
- The [RealWorld example app documentation](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app)

You can also get in touch with the Polylith Team on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ).

<h1>indicators-service</h1>

<p>Add your workspace documentation here...</p>

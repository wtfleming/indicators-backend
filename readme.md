# indicators-service

Provides a JSON based REST microservice providing search capabilities on an open source intelligence feed provided by AlienVault OTX.

Built in Clojure using [Polylith](https://polylith.gitbook.io/polylith)

<img src="logo.png" width="30%" alt="Polylith" id="logo">

## Requirements

- A recent JVM (Java Virtual machine) I developed with 21.
- A recent Clojure CLI https://clojure.org/guides/install_clojure
- Git https://git-scm.com/book/en/v2/Getting-Started-Installing-Git

For development or to run tests install the Polylith CLI tool https://cljdoc.org/d/polylith/clj-poly/0.2.19/doc/install

You do not need the CLI tool if you just want to run the server.

## Running locally

Start a local server

```sh
$ ./start-dev-server.sh
```

Navigate to a page like

http://localhost:8080/indicators?type=YARA

http://localhost:8080/indicators

http://localhost:8080/indicators/280142346

## Endpoints
Return a document by its ID.
`GET /indicators/:id`

Return all documents
`GET /indicators`

Return all documents by its type.
`GET /indicators?type=IPv4`

Valid values for `type` are one of
> URL hostname FileHash-SHA256 FileHash-SHA1 IPv4 CVE email YARA FileHash-MD5 domain


## Running tests

```sh
$ poly test
```


## Running in Docker
To run this server in a docker container run these commands


```sh
# Build the server into an uberjar
$ ./build-uberjar.sh

# Build the docker image
$ docker build -t indicators-backend .

# Start the server in a docker container
$ docker run -p 8080:8080 -it --rm indicators-backend
```

Verify it is running by navigating to a page like http://localhost:8080/indicators?type=YARA


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


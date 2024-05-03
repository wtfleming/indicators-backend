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



<img src="logo.png" width="30%" alt="Polylith" id="logo">

The Polylith documentation can be found here:

- The [high-level documentation](https://polylith.gitbook.io/polylith)
- The [poly tool documentation](https://cljdoc.org/d/polylith/clj-poly/CURRENT)
- The [RealWorld example app documentation](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app)

You can also get in touch with the Polylith Team on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ).

<h1>indicators-service</h1>

<p>Add your workspace documentation here...</p>

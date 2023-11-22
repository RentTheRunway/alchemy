[< back to Manual](../manual.md)

#Alchemy Example, Step by Step

The `alchemy-example` module provides you with an example application that uses the `alchemy-core`
module to create, configure and query experiments.

Keep in mind that
the [Morphia configuration](https://morphia.dev/morphia/2.4/configuration.html#_configuration) is
provided through the `resources/META-INF/morphia-config.properties` file in the implementing
service, otherwise it
won't be able to start. The error will look like this:

```
com.google.inject.CreationException: Unable to create injector, see the following errors:

1) failed to configure mapper: Command failed with error 13 (Unauthorized): 'not authorized on morphia to execute command { insert: "Metadata", ordered: true, txnNumber: 1, $db: "morphia", lsid: { id: UUID("9983b63c-eb8e-455d-9188-0853d145c3f5") } }' on server my-server.mongodb.net:27017. The full response is {"operationTime": {"$timestamp": {"t": 1700683613, "i": 1}}, "ok": 0.0, "errmsg": "not authorized on morphia to execute command { insert: \"Metadata\", ordered: true, txnNumber: 1, $db: \"morphia\", lsid: { id: UUID(\"9983b63c-eb8e-455d-9188-0853d145c3f5\") } }", "code": 13, "codeName": "Unauthorized", "$clusterTime": {"clusterTime": {"$timestamp": {"t": 1700683613, "i": 1}}, "signature": {"hash": {"$binary": {"base64": "95Hx0k491/WktqeA7sWHyKFJXLM=", "subType": "00"}}, "keyId": 7253867749087117314}}}
  at AlchemyModule.configure(AlchemyModule.java:69)

1 error

======================
Full classname legend:
======================
AlchemyModule: "io.rtr.alchemy.service.guice.AlchemyModule"
========================
End of classname legend:
========================
```
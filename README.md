# RHQ plugin to monitor Glassfish 3.1.2.2 via its REST interface.

The plugin is extremely basic at the moment and serves as proof of
concept for agent plugins that use the JAX-RS 2.0 client framework
to talk to a remote REST server.

## Notes

The Glassifish server needs to have its monitoring via REST enabled
via the GF admin console. In the future this could be incorporated
into the initial setup of the plugin after discovery.

The plugin also does not yet support authentication.

The discovery assumes a Glassfish Server on `localhost:4848`. No 
process is done. Manual import is defined, but has not yet been
tested.

## Generator

The class misc/Generator can be used to generate plugin-descriptor snippets
by passing a start path (relative to `http://localhost:4848/monitoring/domain/server` ).
The generator then pulls the information from the REST-interface and recursively
and writes the plugin-descriptor snippet to stdout.

# Condition Factory Design

In order to create more complex Conditions easily a configurable Factory is called for. This allows integration of Conditions in a Systems without the requirement to actually register a Condition Service.

The implementation implementation of the `ConditionFactory` should register a `org.osgi.service.cm.ManagedServiceFactory` with the service PID `osgi.conditionfactory`. A configuration it might receive via ConfigurationAdmin can contain two lists of target filter. One for filters that MUST find at least one Service each in order to have a Condition registered. The other one represents a List of filters, that MUST NOT find any matching service. The moment all the included and non of the exclude filters match a Service, a Condition is registered with the given identifier and the additional properties. If any of the include Services go away or an excluded Service becomes available, this Condition must be unregistered.

## List of properties

Name | Value
------------ | -------------
`osgi.condition.identifier` | The `Constants.CONDITION_ID` that will be set to the condition that will be registered if all filters are satisfied
`osgi.condition.properties.*` | Properties like this will be registered with the condition if all filters are satisfied. The key will be the * part.
`osgi.condition.match.all` | An optional list of valid target filters. A Condition will be registered if each filter finds at least one matching service.
`osgi.condition.match.none` | An optional list of valid target filters. A Condition will not be registered if any filter finds at least one matching service.

## Example

An Example Configuration utilizing the Configurator would look like as follows:

```json
{
    ":configurator:resource-version": 1,
    "osgi.conditionfactory~test" : {
        "osgi.condition.identifier" : "resulting.condition.id",
        "osgi.condition.properties.custom.condition.prop" : "my.property",
        "osgi.condition.match.all" : [
            "(&(objectClass=org.foo.Bar)(my.prop=foo))",
            "(my.prop=bar)"
        ],
        "osgi.condition.match.none" : [
            "(&(objectClass=org.foo.Fizz)(my.prop=buzz))"
        ]
    }
}
```

# OSGi TCK

## Checkout sources

    git clone https://osgi.org/git/build.git osgi-build

## Build

Prerequisite is an installed JDK 8.

    ./gradlew

## Only run tests for a specific reference implementation

For example to run only tests for remote service admin run this command:

    ./gradlew org.osgi.test.cases.remoteserviceadmin:check --rerun-tasks

## IDE Setup

Prerequisites are Eclipse in Variant "Eclipse IDE for Java Developers" and Bndtools 4.1 snapshot.
For bdntools use the update site of the development snapshots like describes at [bndtools installation](https://bndtools.org/installation.html).

Before importing the project into eclipse it is recommended to do a full build like described above.
Inside Eclipse use `Import... / Existing projects into workspace`.

## Run / Debug tests in eclipse

Test classes can be run using run as "Bnd OSGi Test launcher (junit)".

##  Switching to a new version of a reference implementation

Update necessary versions in `cnf/central.mvn` and add local snapshots in a new file local.mvn.
Eventually you will also need to update the .lib file of the reference impl inside `cmf/repo`.

When running a test the installed bundle versions are shown. So you can validate if the switch worked.

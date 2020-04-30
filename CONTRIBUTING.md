# Contributing to OSGi

Here are instructions to get you started. They are probably not perfect, please let us know if anything feels wrong or incomplete.

## Build Environment

The only thing you need to build OSGi is Java.
We require at Java 8. If your main JDK is higher then 8 you can set it on each run by adding `-Dorg.gradle.java.home=<pathToYouJDK>` to the Gradle command.

We use Gradle to build and the repo includes `gradlew`.

- `./gradlew :build` - Builds and releases the projects into `cnf/generated/repo`.
- `./gradlew :osgi.specs:specifications` - Builds the specifications.

We use [GitHub Actions](https://github.com/osgi/osgi/actions?query=workflow%3A%22CI%20Build%22) for continuous integration and the repo includes a `.github/workflows/cibuild.yml` file to build via GitHub Actions.

### Running a single CT

For example, to run the CT for Remote Service Admin run this command:

    ./gradlew :org.osgi.test.cases.remoteserviceadmin:check

### Building the Specifications

The specifications can be build in different formats. Currently available are `html` and `pdf`. You can also build a zip file of the complete html format.

The following tasks can be used:

- `./gradlew :osgi.specs:core.pdf`
- `./gradlew :osgi.specs:core.html`
- `./gradlew :osgi.specs:core.zip`
- `./gradlew :osgi.specs:cmpn.pdf`
- `./gradlew :osgi.specs:cmpn.html`
- `./gradlew :osgi.specs:cmpn.zip`

## IDE Setup

Prerequisites are Eclipse ("Eclipse IDE for Java Developers") and Bndtools.

Before importing the project into eclipse it is recommended to do a full build like described above. Inside Eclipse, use `Import... / Existing projects into workspace`.

### Run / Debug tests in eclipse

The CT tests for a project can be run using Run As "Bnd OSGi Test launcher (junit)".

## Workflow

We use [git triangular workflow](https://github.blog/2015-07-29-git-2-5-including-multiple-worktrees-and-triangular-workflows/).
This means you should not push contributions directly into the [main OSGi repo](https://github.com/osgi/osgi).
All contributions should come in through pull requests.
So each contributor will need to [fork the main OSGi repo](https://github.com/osgi/osgi/fork) on GitHub.
All contributions are made as commits to your fork.
Then you submit a pull request to have them considered for merging into the main OSGi repo.

### Setting up the triangular workflow

After forking the main OSGi repo on GitHub, you can clone the main repo to your system:

    git clone https://github.com/osgi/osgi.git

This will clone the main repo to a local repo on your disk and set up the `origin` remote in Git.
Next you will set up the the second side of the triangle to your fork repo.

    cd osgi
    git remote add fork git@github.com:github-user/osgi.git

Make sure to replace the URL with the SSH URL to your fork repo on GitHub.
Then we configure the local repo to push your commits to the fork repo.

    git config remote.pushdefault fork

So now you will pull from `origin`, the main repo, and push to `fork`, your fork repo.
This option requires at least Git 1.8.4. It is also recommended that you configure

    git config push.default simple

unless you are already using Git 2.0 where it is the default.

Finally, the third side of the triangle is pull requests from your fork repo to the
main repo.

## Contribution guidelines

### Conventions

Fork the repo and make changes on your fork in a feature branch:

- If it's a bugfix branch, name it XXX-something where XXX is the number of the issue
- If it's a feature branch, create an enhancement issue to announce your intentions, and name it XXX-something where XXX is the number of the issue.

Pull requests descriptions should be as clear as possible and include a reference to all the issues that they address.

Pull requests must not contain commits from other users or branches.

Commit messages must start with a short summary (max. 50 chars) written in the imperative, followed by an optional, more detailed explanatory text which is separated from the summary by an empty line.

    index: Remove absolute URLs from the OBR index

    The url for the root was missing a trailing slash. Using File.toURI to
    create an acceptable url.

Code review comments may be added to your pull request.
Discuss, then make the suggested modifications and push the amended commits to your feature branch.
Be sure to post a comment after pushing.
The new commits will show up in the pull request automatically, but the reviewers may not be notified unless you comment.

Before the pull request is merged, make sure that you squash your commits into logical units of work using `git rebase -i` and `git push --force`.
After every commit, the test suite should be passing.
Include documentation changes in the same commit so that a revert would remove all traces of the feature or fix.

Commits that fix or close an issue should include a reference like `Closes #XXX` or `Fixes #XXX`, which will automatically close the issue when merged.

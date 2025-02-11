# Contributing to the OSGi Specification Project

Here are instructions to get you started.
They are probably not perfect, please let us know if anything feels wrong or incomplete.

## Project

[https://github.com/osgi/osgi](https://github.com/osgi/osgi) is the main git repository for the [OSGi Specification Project](https://projects.eclipse.org/projects/technology.osgi).
Our primary written communication channel is the project's email list: [osgi-dev@eclipse.org](https://accounts.eclipse.org/mailing-list/osgi-dev).
We also have a [Slack workspace](https://osgiwg.slack.com) for more informal communications.
Contact a project committer for an invite to the Slack workspace.

Issues can be reported in the [GitHub issue tracker](https://github.com/osgi/osgi/issues).

## Build Environment

The only thing you need to build OSGi is Java.
We require Java 8.
If your main JDK is higher than 8 you can set it on each run by adding `-Dorg.gradle.java.home=<pathToYourJDK8>` to the Gradle command.

### Building with gradle

We use Gradle to build and the repository includes `gradlew`.

- `./gradlew :build :publish` - Builds and releases the artifacts into `cnf/generated/repo`.
- `./gradlew :osgi.specs:specifications` - Builds the specifications into `osgi.specs/generated`.

### Building with Maven (experimental)

As an alternative, you can use Maven to build the sources, this is currently experimental and might not work for all but most features
and is suitable for example when adjusting only small parts of the code.

- go into the directory of interest you want to build (e.g. `osgi.core`)
- `mvn clean verify` compiles and possibly run tests

Tests can be skipped with `-DskipTests`, multiple projects can be build with `-pl <list of projects>` (e.g. `-pl osgi.core,org.osgi.dto`), 
add `-T1C` to speedup build when building multiple projects at once.

### CI Verification

We use [GitHub Actions](https://github.com/osgi/osgi/actions?query=workflow%3A%22CI%20Build%22) for continuous integration and the repo includes a `.github/workflows/cibuild.yml` file to build via GitHub Actions.

### Building the Specifications

The specifications can be built in different formats.
Currently available are `html` and `pdf`.
You can also build a zip file of the complete html format.

The following tasks can be used:

- `./gradlew :osgi.specs:core.pdf`
- `./gradlew :osgi.specs:core.html`
- `./gradlew :osgi.specs:core.zip`
- `./gradlew :osgi.specs:cmpn.pdf`
- `./gradlew :osgi.specs:cmpn.html`
- `./gradlew :osgi.specs:cmpn.zip`

## IDE Setup

Prerequisites are Eclipse ("Eclipse IDE for Java Developers") and [Bndtools](https://bndtools.org/).

Before importing the project into Eclipse, it is recommended to do a full build as described above.
Then use `Import...` > `Existing Projects into Workspace` in Eclipse.

### Run / Debug a TCK

In Eclipse, the TCK tests for a project can be run using `Run As` >  `Bnd OSGi Test Launcher (JUnit)` or debugged using `Debug As`/`Debug As` >  `Bnd OSGi Test Launcher (JUnit)`.

From the command line, you use the `testOSGi` Gradle task of the project. For example, run the TCK for Remote Service Admin using this command:

```script
./gradlew :org.osgi.test.cases.remoteserviceadmin:testOSGi
```

When running the TCK from the TCK project folder, you are running the TCK in git working directory while the TCK must run, for compatibility testing purposes, outside of the development environment.
The `osgi.tck` project packages the TCKs into the form for which users will be expected to run the TCKs.
The CI build will execute the TCKs using the packaged form built by the `osgi.tck` project rather than running the `testOSGi` tasks.

## Workflow

We use [git triangular workflow](https://github.blog/2015-07-29-git-2-5-including-multiple-worktrees-and-triangular-workflows/).
This means you should not push contributions directly into the [main OSGi repository](https://github.com/osgi/osgi).
All contributions should come in through pull requests.
So each contributor will need to [fork the main OSGi repository](https://github.com/osgi/osgi/fork) on GitHub.
All contributions are made as commits to your fork.
Then you submit a pull request to have them considered for merging into the main OSGi repository.

### Setting up the triangular workflow

After forking the main OSGi repository on GitHub, you can clone the main repository to your system:

```script
git clone https://github.com/osgi/osgi.git
```

This will clone the main repository to a local repository on your disk and set up the `origin` remote in Git.
Next you will set up the the second side of the triangle to your fork repository.

```script
cd osgi
git remote add fork git@github.com:github-user/osgi.git
```

Make sure to replace the URL with the SSH URL to your fork repository on GitHub.
Then we configure the local repository to push your commits to the fork repository.

```script
git config remote.pushdefault fork
```

So now you will pull from `origin`, the main repository, and push to `fork`, your fork repository.
This option requires at least Git 1.8.4
It is also recommended that you configure

```script
git config push.default simple
```

unless you are already using Git 2.0 where it is the default.

Finally, the third side of the triangle is pull requests from your fork repository to the
main repository.

## Contribution guidelines

For the development of non-trivial new features, this project will first undertake a requirements discussion and, if the requirements discussion concludes successfully, then a design discussion.
By _new feature_, we mean a new specification or a non-trivial enhancement to an existing specification.

Requirements discussions and design discussions should be used whenever there is a benefit to the project for the clarity gained by such discussions.
For a minor enhancement to an existing specification, we can sometimes skip the requirements discussion and include the requirements in the design discussion.

If the design discussion concludes successfully, we can then move to integrate the new feature into the project which requires specification writing and API and TCK development as well as coordination with the development of a [compatible implementation](https://www.eclipse.org/projects/handbook/#specifications-implementations).
Each discussion will occur in its own branch of the git repository (see below for specific details).
This allows the discussion document along with any supporting code to be committed in the branch and for contributors to the discussion to make pull requests against the branch to suggest changes.

The requirement discussion and design discussion documents are stored in the `.design` folder of the repository.
This is done so the folder is at the top of the GitHub repository web page to make it easy to find while generally keeping the folder out of sight during normal development.
From time-to-time, generally when making a specification release, the `.design` folder will be cleaned up to remove older documents whose purpose has been served.

### Requirements discussion

The purpose of the requirements discussion is to engage the project committers in the discussion about the new feature and to more properly understand the terminology, purpose, use cases, and requirements for the new feature.
A new requirements discussion must be started by first opening a new tracking [issue](https://github.com/osgi/osgi/issues) and labeling the issue with the [`requirements`](https://github.com/osgi/osgi/labels/requirements) label.
Then a new branch should be created from the tip of the `main` branch with the branch name _requirements/XXX_ where _XXX_ is the number of the created tracking issue.

In this new branch, create a `requirements-XXX.md` (or `requirements-XXX.adoc`) requirements document in the `.design` folder.

```script
git checkout -b requirements/XXX main
vi .design/requirements-XXX.md
git add .design/requirements-XXX.md
git commit -s -m "First draft of requirements for new Widget specification"
```

The requirements document should include the following items for the requirements discussion:

- Terminology - The new feature may use terminology new to the project committers or that may have multiple meanings.
- Problem Description - What problem or problems will the new feature address or solve?
- Use Cases - Provide several use cases which can demonstrate the actors and how they will use the new feature to address the problem(s).
- Requirements - This is a list of requirements the new feature is to address.

Once your _requirements/XXX_ branch has your commit with the new requirements document, push this branch your fork and make a pull request to the [main OSGi repository](https://github.com/osgi/osgi).
This pull request will be used to confirm you have signed the [ECA](#legal-considerations) and will be used as the basis for creating the _requirements/XXX_ branch in the main OSGi repository.
A project committer must [create the new _requirements/XXX_ branch](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-and-deleting-branches-within-your-repository#creating-a-branch) in the main OSGi repository and then [change the base branch of the pull request](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/changing-the-base-branch-of-a-pull-request) to the newly created _requirements/XXX_ branch in the main OSGi repository.
At this point, the pull request can be merged into the main OSGi repository.

Discussion on the requirements document can take place in the created issue and updates to the requirements document can be made via additional pull requests against the _requirements/XXX_ branch.

To successfully conclude the requirements discussion, the project committers must call a [lazy consensus vote](https://community.apache.org/committers/lazyConsensus.html) of the project committers via an email to the `osgi-dev@eclipse.org` mail list.
The lazy consensus vote must remain open for at least 72 hours.

If the requirements discussion successfully concludes, the _requirements/XXX_ branch is merged into the `main` branch, the tracking issue _XXX_ is closed, and work can then proceed to a design discussion.

### Design discussion

The purpose of the design discussion is to engage the project committers in the discussion about the design for a new feature and its place in the overall OSGi architecture.
We want to ensure that sufficient up-front discussion of a new feature design is done in a branch before committing work to the `main` branch.
A new design discussion must be started by first opening a new tracking [issue](https://github.com/osgi/osgi/issues) and labeling the issue with the [`design`](https://github.com/osgi/osgi/labels/design) label.
Then a new branch should be created from the tip of the `main` branch with the branch name _design/XXX_ where _XXX_ is the number of the created design tracking issue.

In this new branch, create a `design-XXX.md` (or `design-XXX.adoc`) design document in the `.design` folder.

```script
git checkout -b design/XXX main
vi .design/design-XXX.md
git add .design/design-XXX.md
git commit -s -m "First draft of design for new Widget specification"
```

The design document should include the following items for the design discussion:

- Requirements - This is a list of requirements the new feature is to address.
This can be a reference to a previously created requirements document or a list of requirements that the design will address.
- Technical Solution - What is the design?
This should explain the design and how it works and fits into the OSGi architecture.
This is mainly for discussion within the project and is not necessarily the text that would go in the final specification.
But it is the starting point for that.
- Data Transfer Objects - DTOs are defined and used in many specifications.
Should this new design define any DTOs?

Once your _design/XXX_ branch has your commit with the new design document, along with any supporting API, code, etc., push this branch your fork and make a pull request to the [main OSGi repository](https://github.com/osgi/osgi).
This pull request will be used to confirm you have signed the [ECA](#legal-considerations) and will be used as the basis for creating the _design/XXX_ branch in the main OSGi repository.
A project committer must [create the new _design/XXX_ branch](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-and-deleting-branches-within-your-repository#creating-a-branch) in the main OSGi repository and then [change the base branch of the pull request](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/changing-the-base-branch-of-a-pull-request) to the newly created _design/XXX_ branch in the main OSGi repository.
At this point, the pull request can be merged into the main OSGi repository.

Discussion on the design document can take place in the created issue and updates to the design document and any supporting code can be made via additional pull requests against the _design/XXX_ branch.

To successfully conclude the design discussion, the project committers must call a vote of the project committers via an email to the `osgi-dev@eclipse.org` mail list.
The vote must remain open for at least 72 hours.
To succeed, the vote must have at least 3 votes with more `+1` than `-1`.
Any `-1` vote must be accompanied by an explanation of the vote.

If the design discussion successfully concludes, the _design/XXX_ branch is merged into the `main` branch, the tracking issue _XXX_ is closed, and work can then proceed to specification writing and API and TCK development as well as coordinating the development of a compatible implementation.

### Create issues

Any significant improvement should be documented as [a GitHub issue](https://github.com/osgi/osgi/issues) before anybody starts working on it.

Please take a moment to check that an issue doesn't already exist documenting your bug report or improvement proposal.
If it does, it never hurts to add a quick 👍 reaction.
This will help prioritize the most common problems and requests.

### Pull requests are always welcome

We are always thrilled to receive pull requests, and do our best to process them as fast as possible.

If your pull request is not accepted on the first try, don't be discouraged!
If there's a problem with the implementation, hopefully you received feedback on what to improve.

### Conventions

Fork the repository and make changes on your fork in a feature branch:

- If it's a bug fix branch, name it _issues/XXX_ where XXX is the number of the issue.
- If it's a requirements branch, name it _requirements/XXX_ where XXX is the number of the requirements tracking issue.
- If it's a design branch, name it _design/XXX_ where XXX is the number of the design tracking issue.

Write clean code.
Universally formatted code promotes ease of writing, reading, and maintenance.
We use Eclipse and each project has Eclipse `.settings` which will properly format the code.
Make sure to avoid unnecessary white space changes which complicate diffs and make reviewing pull requests much more time consuming.

Pull requests descriptions should be as clear as possible and include a reference to all the issues that they address.

Pull requests must not contain commits from other users or branches.

Commit messages must start with a short summary (max. 50 chars) written in the imperative, followed by an optional, more detailed explanatory text which is separated from the summary by an empty line.

```
index: Remove absolute URLs from the OBR index

The url for the root was missing a trailing slash. Using File.toURI to
create an acceptable url.
```

Code review comments may be added to your pull request.
Discuss, then make the suggested modifications and push the amended commits to your feature branch.
Be sure to post a comment to the pull request after pushing.
The new commits will show up in the pull request automatically, but the reviewers may not be notified unless you comment.

Before the pull request is merged, make sure that you squash your commits into logical units of work using `git rebase -i` and `git push --force`.
After every commit, the test suite should be passing.
Include documentation changes in the same commit so that a revert would remove all traces of the feature or fix.

Commits that fix or close an issue should include a reference like `Closes #XXX` or `Fixes #XXX`, which will automatically close the issue when merged.

### Sign your work

Sign off on your commit in the commit comment footer.
By doing this, you assert original authorship of the commit and that you are permitted to contribute it.
This can be automatically added to your commit by passing `-s` to `git commit`, or by hand adding the following line to the footer of the commit.

```
Signed-off-by: Full Name <email>
```

Remember, if a blank line is found anywhere after the `Signed-off-by` line, the `Signed-off-by:` will be considered outside of the footer, and will fail the automated Signed-off-by validation.

It is important that you read and understand the legal considerations found below when signing off or contributing any commit.

### Merge approval

The maintainers will review your pull request and, if approved, will merge into the main repository.

If your pull request was originally a draft, don't forget to remove the draft status to signal to the maintainers that it is ready for review.

## Legal considerations

Please read the [Eclipse Foundation policy on accepting contributions via Git](http://wiki.eclipse.org/Development_Resources/Contributing_via_Git).

Your contribution cannot be accepted unless you have a signed [ECA - Eclipse Foundation Contributor Agreement](http://www.eclipse.org/legal/ECA.php) in place.

Here is the checklist for contributions to be _acceptable_:

1. [Create an account at Eclipse](https://dev.eclipse.org/site_login/createaccount.php).
2. Add your GitHub user name in your account settings.
3. [Log into the project's portal](https://projects.eclipse.org/) and sign the ["Eclipse ECA"](https://projects.eclipse.org/user/sign/cla).
4. Ensure that you [_sign-off_](https://wiki.eclipse.org/Development_Resources/Contributing_via_Git#Signing_off_on_a_commit) your Git commits.
5. Ensure that you use the _same_ email address as your Eclipse account in commits.
6. Include the appropriate copyright notice and license at the top of each file.

Your signing of the ECA will be verified by a webservice called 'ip-validation' that checks the email address that signed-off on your commits has signed the ECA.
**Note**: This service is case-sensitive, so ensure the email that signed the ECA and that signed-off on your commits is the same, down to the case.

### Copyright Notice and Licensing Requirements

**It is the responsibility of each contributor to obtain legal advice, and to ensure that their contributions fulfill the legal requirements of their organization. This document is not legal advice.**

OSGi is licensed under the the Apache License, Version 2.0.
Any previously unlicensed contribution should be released under the same license.

- If you wish to contribute code under a different license, you must consult with a committer before contributing.
- For any scenario not covered by this document, please discuss the copyright notice and licensing requirements with a committer before contributing.

The template for the copyright notice and license is as follows:

```java
/*
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
```

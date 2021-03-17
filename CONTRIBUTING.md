# Contributing to the OSGi Specification Project

Here are instructions to get you started. They are probably not perfect, please let us know if anything feels wrong or incomplete.

## Project

[https://github.com/osgi/osgi](https://github.com/osgi/osgi) is the main git repository for the [OSGi Specification Project](https://projects.eclipse.org/projects/technology.osgi).
Our primary written communication channel is the project's email list: [osgi-dev@eclipse.org](https://accounts.eclipse.org/mailing-list/osgi-dev).
We also have a [Slack workspace](https://osgiwg.slack.com) for more informal communications.
Contact a project committer for an invite to the Slack workspace.

Issues can be reported in the [GitHub issue tracker](https://github.com/osgi/osgi/issues).
Please include the steps required to reproduce the problem if possible and applicable.
This information will help us review and address the issue more quickly.

## Build Environment

The only thing you need to build OSGi is Java.
We require Java 8. If your main JDK is higher then 8 you can set it on each run by adding `-Dorg.gradle.java.home=<pathToYourJDK8>` to the Gradle command.

We use Gradle to build and the repo includes `gradlew`.

- `./gradlew :build` - Builds and releases the artifacts into `cnf/generated/repo`.
- `./gradlew :osgi.specs:specifications` - Builds the specifications into `osgi.specs/generated`.

We use [GitHub Actions](https://github.com/osgi/osgi/actions?query=workflow%3A%22CI%20Build%22) for continuous integration and the repo includes a `.github/workflows/cibuild.yml` file to build via GitHub Actions.

### Running a single TCK

For example, to run the TCK for Remote Service Admin in development mode, run this command:

    ./gradlew :org.osgi.test.cases.remoteserviceadmin:check

### Building the Specifications

The specifications can be built in different formats. Currently available are `html` and `pdf`. 
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

Before importing the project into Eclipse it is recommended to do a full build like described above. Inside Eclipse, use `Import... / Existing Projects into Workspace`.

### Run / Debug tests in eclipse

The TCK tests for a project can be run using Run As "Bnd OSGi Test Launcher (JUnit)".

## Workflow

We use [git triangular workflow](https://github.blog/2015-07-29-git-2-5-including-multiple-worktrees-and-triangular-workflows/).
This means you should not push contributions directly into the [main OSGi repo](https://github.com/osgi/osgi).
All contributions should come in through pull requests.
So each contributor will need to [fork the main OSGi repo](https://github.com/osgi/osgi/fork) on GitHub.
All contributions are made as commits to your fork.
Then you submit a pull request to have them considered for merging into the main OSGi repo.

### Setting up the triangular workflow

After forking the main OSGi repo on GitHub, you can clone the main repo to your system:

```
git clone https://github.com/osgi/osgi.git
```

This will clone the main repo to a local repo on your disk and set up the `origin` remote in Git.
Next you will set up the the second side of the triangle to your fork repo.

```
cd osgi
git remote add fork git@github.com:github-user/osgi.git
```

Make sure to replace the URL with the SSH URL to your fork repo on GitHub.
Then we configure the local repo to push your commits to the fork repo.

```
git config remote.pushdefault fork
```

So now you will pull from `origin`, the main repo, and push to `fork`, your fork repo.
This option requires at least Git 1.8.4. It is also recommended that you configure

```
git config push.default simple
```

unless you are already using Git 2.0 where it is the default.

Finally, the third side of the triangle is pull requests from your fork repo to the
main repo.

## Contribution guidelines

### Pull requests are always welcome

We are always thrilled to receive pull requests, and do our best to process them as fast as possible.
Not sure if that typo is worth a pull request?
Do it! We will appreciate it.

If your pull request is not accepted on the first try, don't be discouraged!
If there's a problem with the implementation, hopefully you received feedback on what to improve.

### Create issues

Any significant improvement should be documented as [a GitHub issue](https://github.com/osgi/osgi/issues) before anybody starts working on it.

### ... but check for existing issues first

Please take a moment to check that an issue doesn't already exist documenting your bug report or improvement proposal.
If it does, it never hurts to add a quick 👍 reaction.
This will help prioritize the most common problems and requests.

### Conventions

Fork the repo and make changes on your fork in a feature branch:

- If it's a bugfix branch, name it issues/XXX where XXX is the number of the issue
- If it's a feature branch, create an enhancement issue to announce your intentions, and name it issues/XXX where XXX is the number of the issue.

Write clean code. Universally formatted code promotes ease of writing, reading, and maintenance.
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

### Large changes/Work-In-Progress

Sometimes for big changes/feature additions, you may wish to submit a pull request before it is fully ready to merge, in order to solicit feedback from the core developers and ensure you're on the right track before proceeding too far.
In this case, you can submit a pull request and mark it as a draft in GitHub.

Once your pull request is ready for consideration to merge, remove the draft status from the pull request to signal this fact to the core team.
While the pull request is marked as draft the maintainers are unlikely to know that it is ready, the review process won't start and your branch won't get merged.

### Merge approval

The maintainers will review your pull request and, if approved, will merge into the main repo.

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

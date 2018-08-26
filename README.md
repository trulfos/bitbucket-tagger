# Bitbucket tagger for Bamboo

This plugin pushes a tag to a linked Bitbucket repository. Note that the
functionality is quite limited and assumes the repo
* already contains the commit to be tagged,
* is set for connection through SSH, and
* uses key authentication.

The plugin may possibly work with other git repos as well, depending on how
Bamboo handles such repositories.

## Usage
Download the Atlassian Plugin SDK and run `atlas-package` to compile and package
this plugin.  Log into Bamboo and install the plugin by selecting the jar file
from the `target` directory.

One example use case is releasing npm packages to an internal npm repository
using Bamboo. A possible set up would be to
1. remove the version from `package.json`,
2. build the package in the Bamboo build plan,
3. run `npm version $bamboo_deploy_version` in the deployment project, and
4. add a bitbucket tagger task from this plugin configured to push a tag
   to bitbucket, using `refs/tags/${bamboo.deploy.version}` as tag name and
   `${bamboo.planRepository.0.revision}` as ref.

## Development
Start Bamboo by running `atlas-run`. To quickly reload the plugin, run
`atlas-package` from another terminal.

Contributions in the form of pull requests, suggestions or bug reports are very
welcome.

## License
This plugin is released under the MIT license. See the LICENSE file for the
entire license.

## Links
https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK

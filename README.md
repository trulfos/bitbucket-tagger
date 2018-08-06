# Bitbucket tagger for Bamboo

This plugin pushes a tag to a linked Bitbucket repository. Note that it's
currently quite limited and only support ssh with key authentication.

## Usage
Download the Atlassian Plugin SDK and run `atlas-package` to compile and package
this plugin.  Log into Bamboo and install the plugin by selecting the jar file
from the `target` directory.

## Development
Start Bamboo by running `atlas-run`. To quickly reload the plugin, run
`atlas-package`.


## Links
https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK

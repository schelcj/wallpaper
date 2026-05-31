# TODOs

## Bugs

- [x] when setting category the stats still reflect the history of all images
- [x] clearing the category clears the history

## Features

- [-] move categories to the config and drop reading from disk and cli opts
- [-] move config, cache, history to sqlite _(but why...)_
- [ ] add ui for configuration, preview, and setting
- [-] store metadata on the score of the wallpaper when displayed, could include the mtime at the time dispalyed as well
- [ ] detect overall brightness and or tone to favor darker and warmer images if desired
- [x] convert all images to grayscale for B&W feel
- [x] add arg to show the weighting of the current wallpaper _v0.1.1_
- [x] add arg to show stats of wallpapers displayed by category (including how many displayed by category and total)
- [x] handle app-name more generically so a rename is easier
- [x] add arg to display the version number
- [x] guard against running without using `--init`
- [ ] add arg to uninstall to cleanup
- [ ] do not let stack traces go to the user
- [x] add a constants class for name and version for starters
- [ ] add arg to write default config to XDG_CONFIG_DIR
- [x] drop need for `--init` with preflight checks
- [ ] hash gray scale cache file directories or just use a single file. the directory of is going to get out of hand.
- [ ] make `config` a constant instead of loading from disk in every function

## Documentation

- [ ] write introduction in the doc directory (probably should have started there to begin with...)
- [ ] write getting started section in readme
- [ ] write example of running in crontab export the display env var
- [ ] update doc strings to explain why side-effects
- [ ] document using the binary wrapper from the release build

## Deployment

- [x] settle on versions so actions will work
- [x] github action to create uberjar and standalone executable
- [ ] create new workflow for just building and running tests

## Tests

- [ ] setup app mode to ease testing _(i.e. development mode vs. production)_
- [ ] write config namespace tests
- [ ] write history namespace tests
- [ ] write papers namespace tests
- [ ] can i write tests for the core namespace, anything to test?

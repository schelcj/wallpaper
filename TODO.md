# TODOs

## Features

- [x] add arg to show the weighting of the current wallpaper _v0.1.1_
- [ ] add arg to show stats of wallpapers displayed by category (including how many displayed by category and total)
- [ ] detect overall brightness and or tone to favor darker and warmer images if desired
- [ ] handle app-name more generically so a rename is easier
- [ ] store metadata on the score of the wallpaper when displayed, could include the mtime at the time dispalyed as well
- [x] add arg to display the version number
- [x] guard against running without using `--init`
- [ ] add arg to uninstall to cleanup
- [ ] do not let stack traces go to the user
- [ ] add a constants class for name and version for starters

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

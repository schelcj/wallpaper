# Change Log

All notable changes to this project will be documented
in this file. This change log follows the conventions
of [keepachangelog.com](https://keepachangelog.com/).

## [Unreleassd]

## [0.1.6] - 2026-06-21

### Changed

- added `prefligh-check!` to make sure default files and dirs are in place
- updated the config namespace to create a single instance of the config to avoid hitting disk in every function
- create a single gray scale image and overwrite each time to save disk space

### Removed

- no longer need the `--init` command line flag with preflight checks

## [0.1.5] - 2026-05-23

### Changed

- added `fileutils` for basic file path manip _(e.g. basename, dirname, etc)_
- reworked the `config` namespace to merge the default config and the user config
  as well as provide templated paths in the config as placeholder for XDG paths

### Added

- convert wallpaper images to gray-scale based on config option

### Removed

- dropped the `config/init?` preflight test that looks for the user config that
  is now no longer required because the default is merged with any user settings.

## [0.1.4] - 2025-12-27

### Changed

- reworked tiling support so that they can be included in the random selection
- new configuration file param `:tiles-dir` to specify where tiles live

### Removed

- dropped the `--tile` option now that `--image` and random selection will detect a tile
- `wallpaper.papers/display-fullscreen!` and `wallpaper.papers/display-tiled!` removed and replaced with a single `wallpaper.papers/display!` that detects if the image is a tile or not _(currently only by looking at the path of the image compared to the configured tiles directory)_.

## [0.1.3] - 2025-11-02

### Changed

- printing to STDOUT only from the core namespace during arg parsing
- changed the short-opts for a few args and dropped the short opts from others
- move directory filtering to the gather function instead of the prune function

### Added

- stats namespace to begin providing some info on what's been displayed and such
- stats output option `--stats` that prints a table of categories, counts of displayed/available, and percentages.

## [0.1.2] - 2025-10-19

### Added

- constants namespace to define name and version in a single place
- added a `--version` argument

### Fixed

- prevent errors on first run before `--init` is ran

## [0.1.1] - 2025-10-18

### Added

- added the `lein-binplus` plugin to build a binary wrapper
- added [TODOs](./TODO.md) to track what i want to do next
- added `--show-weight` command line option to show the score of the current wallpaper

### Changed

- Completed [README](./README.md)
- Completed this initial Changelog
- renaming functions to match conventions for side-effects

### Removed

- made tests pass for now, not sure how to do them yet

### Fixed

- Order of argument parsing for `--init` on first run

## [0.1.0] - 2025-10-05

Initial _"release"_ wherein most everything works well enough for my uses.
There was never an actual `0.1.0` release/tag.

[Unreleased]: https://github.com/schelcj/wallpaper/tree/main

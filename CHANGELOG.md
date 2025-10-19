# Change Lo

All notable changes to this project will be documented
in this file. This change log follows the conventions
of [keepachangelog.com](https://keepachangelog.com/).

## [Unreleased]

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

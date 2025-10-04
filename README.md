# wallpaper

A simple Clojure application to manage and rotate desktop wallpapers.

## Overview

This project is a learning exercise in Clojure. It provides functionality to:

- Gather wallpapers from directories
- Filter wallpapers based on history
- Assign weights based on file modification time
- Pick a new wallpaper at random
- Maintain a history of used wallpapers

## Features

- Configure directories, history file location, and weights via a configuration file.
- Bundles a default configuration (`resources/config.edn`) for first-time setup.
- Supports building an `uberjar` to run standalone.
- Automatically resets history when no valid wallpapers remain.

## Getting Started

### Requirements

- [Java JDK 8+](https://adoptium.net/)
- [Leiningen](https://leiningen.org/)

### Setup & Run (Development)

Clone the repository:

```bash
git clone https://github.com/schelcj/wallpaper.git
cd wallpaper

# Run with Leiningen:
lein run

# pass args to lein:
lein run -- --help

# build a standalone jar file with:
lein uberjar

# run the jar file
java -jar target/wallpaper-standalone.jar
```

## Configuration

The default configuration is included as [resources/config.edn](./resources/config.edn). Before first run
the `--init` argument should be used to setup the default configuration file and directories.
A default configuration file is placed in `$XDG_CONFIG_DIR/wallpaper/config.edn`. All the default
should be sufficient except the `:wallpaper-dir` value. This is the path where wallpaper files
are stored organized by "category" _(i.e. directories)_. 

After initialization categories of wallpapers should be added with `--add-category <category>`
argument. The `<category>` is a directory within the `:wallpapers-dir`. No wallpapers will be
set until at least one category is added.

Typical settings to change include:

* **:wallpapers-dir** - root wallpapers directory
* **:weights** - map of time thresholds to weight values
* **:setter** - path and options for utility to set the wallaper

## How It Works

1. Reads configuration (bundled or user-provided).
2. Enumerates wallpaper files from configured directories.
3. Prunes already used wallpapers according to history.
4. Computes a “weight” for each candidate based on modification time.
5. Chooses a random wallpaper, weighted by those weights.
6. Updates the history file so the same wallpaper isn’t picked again too soon.
7. If the candidate list is empty, resets history and starts over from all wallpapers.

## Contributing

Contributions, issues, and feature requests are welcome!
Feel free to fork the repo and make a pull request.

## License

This project is licensed under the Eclipse Public License 2.0 see [LICENSE](./LICENSE)
for details.

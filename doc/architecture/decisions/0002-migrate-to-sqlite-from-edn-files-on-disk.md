# 2. migrate to sqlite from edn files on disk

Date: 2026-03-07

## Status

Pending

## Context

When I this was started `edn` files where the simplest method to start with when
just beginning to learn clojure. With a bit more experience now, not all that much really,
I'm considering replacing the `edn` usage with an `sqlite` database.

## Decision

The change that we're proposing or have agreed to implement.

All the configuration, caching, and tracking are done with `edn` files on disk.
Proposing to migrate all, or as much, to an sqlite database.

### Config file changes

* [ ] remove `:category-file`, replaced with db table
* [ ] remove `:sources`, replaced with db table
* [ ] remove `:history`, replaced with db table
* [ ] remove `:current`, replace with db table
* [ ] remove `:previous`, replace with db table
* [ ] remove `:lock-file`, make static constant, no need for this to be configurable
* [ ] make db file path configurable?
  
### Changes to config namespace

* [ ] `construct` needs to load settings from db instead of relying on `io/file` paths
* [ ] `init!` can be pared back to only writing the config and deploying/migrating the db file

### Schema

Initial, and incomplete, schema...

```sql
create table config (
    id integer primary,
);

create table history (
    id integer primary,
    wallpaper_id integer, -- forgeign key to wallpapers.id
    category_id integer, -- feign key to categoryies.id
    displayed_at datetime,
);

-- place to store
create table wallpapers (
    id integer primary,
    file text,
    created_at datetime,
);

-- place to store all the directories
-- could manage this with the `-add-category` and `--remove-category` args
create table categories (
    id integer primary,
    name text,
    created_at datetime,
);

-- place to store the current, previous, weights, timestamps, etc
-- record last scan here so we don't have to scan all the directories every time.
-- current category if set with `--category`
create table meta ();

-- maybe move weights to db but if i do then i have to provide editing interface
create table weights(
    age integer,
    wieght integer,
    description text,
);
```

## Consequences

What becomes easier or more difficult to do and any risks introduced by the change that will need to be mitigated.

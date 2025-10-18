(defproject wallpaper "0.1.1"
  :description "Desktop wallpaper setter"
  :url "https://github.com/schelcj/wallpaper"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.cli "1.1.230"]
                 [ahungry/xdg-rc "0.0.4"]]
  :main wallpaper.core
  :plugins [[lein-binplus "0.6.6"]]
  :bin {:name "wallpaper"}
  :repl-options {:init-ns wallpaper.core}
  :aot [wallpaper.core])

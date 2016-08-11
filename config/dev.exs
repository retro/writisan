use Mix.Config

config :writisan, Writisan.Endpoint,
  http: [port: 4000],
  debug_errors: true,
  code_reloader: true,
  cache_static_lookup: false,
  check_origin: false,
  watchers: [node: ["node_modules/brunch/bin/brunch", "watch", "--stdin"]]

config :writisan, Writisan.Endpoint,
  live_reload: [
    patterns: [
      ~r{priv/static/.*(css|png|jpeg|jpg|gif|svg)$},
      ~r{priv/gettext/.*(po)$},
      ~r{web/views/.*(ex)$},
      ~r{web/templates/.*(eex)$}
    ]
  ]

config :writisan, Writisan.Repo,
  adapter: Ecto.Adapters.Postgres,
  username: "postgres",
  password: "postgres",
  database: "writisan_dev",
  hostname: "localhost",
  pool_size: 10

config :logger, :console, format: "[$level] $message\n"

config :phoenix, :stacktrace_depth, 20

config :ueberauth, Ueberauth.Strategy.Google.OAuth,
  client_id: "672836200010-i32d1ls1bl69hlboa365k897or8mc1h7.apps.googleusercontent.com",
  client_secret: "FoLl4d00B9Z_gd8fo-EE-r53"

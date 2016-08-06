# This file is responsible for configuring your application
# and its dependencies with the aid of the Mix.Config module.
#
# This configuration file is loaded before any dependency and
# is restricted to this project.
use Mix.Config

# Configures the endpoint
config :writisan, Writisan.Endpoint,
  url: [host: "localhost"],
  root: Path.dirname(__DIR__),
  secret_key_base: "oU/u+wIAiAW7Zp0iVxDkgIgbJxSvppkVolZhSuqj05y8OtOBdFcyNf4ORlnLdHeI",
  render_errors: [accepts: ~w(html json)],
  pubsub: [name: Writisan.PubSub,
           adapter: Phoenix.PubSub.PG2]

config :writisan, ecto_repos: [Writisan.Repo]

# Configures Elixir's Logger
config :logger, :console,
  format: "$time $metadata[$level] $message\n",
  metadata: [:request_id]

# Import environment specific config. This must remain at the bottom
# of this file so it overrides the configuration defined above.
import_config "#{Mix.env}.exs"

# Configure phoenix generators
config :phoenix, :generators,
  migration: true,
  binary_id: false

config :ueberauth, Ueberauth,
  providers: [
    google: { Ueberauth.Strategy.Google, [
        default_scope: "email profile",
        access_type: "offline"
      ]}
  ]

config :guardian, Guardian,
  allowed_algos: ["HS512"], # optional
  verify_module: Guardian.JWT,  # optional
  issuer: "Writisan",
  ttl: { 30, :days },
  verify_issuer: true, # optional
  secret_key: "sljfa9283hrasldkfj0awhfals",
  serializer: Writisan.GuardianSerializer

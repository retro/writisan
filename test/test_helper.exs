ExUnit.start

Mix.Task.run "ecto.create", ~w(-r Writisan.Repo --quiet)
Mix.Task.run "ecto.migrate", ~w(-r Writisan.Repo --quiet)
Ecto.Adapters.SQL.begin_test_transaction(Writisan.Repo)


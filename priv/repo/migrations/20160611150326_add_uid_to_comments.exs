defmodule Writisan.Repo.Migrations.AddUidToComments do
  use Ecto.Migration

  def change do
    alter table(:comments) do
      add :uid, :string, null: false
    end
  end
end

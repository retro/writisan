defmodule Writisan.Repo.Migrations.AddDiffToDocuments do
  use Ecto.Migration

  def change do
    alter table(:documents) do
      add :diff, :text
    end
  end
end

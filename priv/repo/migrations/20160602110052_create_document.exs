defmodule Writisan.Repo.Migrations.CreateDocument do
  use Ecto.Migration

  def change do
    create table(:documents) do
      add :author_id, references(:users, on_delete: :nothing), null: false
      add :bucket_id, references(:buckets, on_delete: :nothing), null: false
      add :prev_version_id, references(:documents, on_delete: :nothing)

      add :hash, :string, null: false
      add :content, :text, null: false
      add :parts, {:array, :text}, null: false

      timestamps
    end

    create unique_index(:documents, [:hash])
  end
end

defmodule Writisan.Repo.Migrations.CreateDocuments do
  use Ecto.Migration

  def change do
    create table(:documents) do
      add :author_id, references(:users, on_delete: :nothing), null: false
      
      add :hash, :string, null: false
      add :content, :text, null: false
      add :parts, {:array, :text}, null: false

      timestamps
    end

    create index(:documents, [:hash])
  end
end

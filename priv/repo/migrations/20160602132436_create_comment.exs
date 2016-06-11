defmodule Writisan.Repo.Migrations.CreateComment do
  use Ecto.Migration

  def change do
    create table(:comments) do
      add :author_id, references(:users, on_delete: :nothing), null: false
      add :document_id, references(:documents, on_delete: :delete_all), null: false

      add :content, :text, null: false
      add :idx, :integer
      add :subpath, :map

      timestamps
    end
  end
end

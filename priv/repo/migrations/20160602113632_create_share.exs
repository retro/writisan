defmodule Writisan.Repo.Migrations.CreateShare do
  use Ecto.Migration

  def change do
    create table(:shares) do
      add :user_id, references(:users, on_delete: :delete_all), null: false
      add :document_id, references(:documents, on_delete: :delete_all), null: false
      add :role, :string, default: "reviewer"

      timestamps
    end

    create unique_index(:shares, [:user_id, :document_id])
  end
end

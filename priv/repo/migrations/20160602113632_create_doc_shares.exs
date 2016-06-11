defmodule Writisan.Repo.Migrations.CreateDocShares do
  use Ecto.Migration

  def change do
    create table(:doc_shares) do
      add :user_id, references(:users, on_delete: :delete_all), null: false
      add :document_id, references(:documents, on_delete: :delete_all), null: false
      add :role, :string, default: "reviewer"

      timestamps
    end

    create index(:doc_shares, [:user_id, :document_id])
  end
end

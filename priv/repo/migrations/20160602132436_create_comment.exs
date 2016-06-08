defmodule Writisan.Repo.Migrations.CreateComment do
  use Ecto.Migration

  def change do
    create table(:comments) do
      add :author_id, references(:users, on_delete: :nothing), null: false
      add :content, :text, null: false
      add :idx, :integer

      timestamps
    end

  end
end

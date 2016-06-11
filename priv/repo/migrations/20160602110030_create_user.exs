defmodule Writisan.Repo.Migrations.CreateUser do
  use Ecto.Migration

  def change do
    create table(:users) do
      add :name, :string
      add :email, :string
      add :avatar, :string
      add :uid, :string
      add :token, :string
      add :refresh_token, :string
      add :token_expires_at, :datetime

      timestamps
    end
  end
end

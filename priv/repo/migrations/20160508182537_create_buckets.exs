defmodule Writisan.Repo.Migrations.CreateBuckets do
  use Ecto.Migration

  def change do
    create table(:buckets) do
      add :name, :string

      timestamps
    end
  end
end

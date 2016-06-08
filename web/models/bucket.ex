defmodule Writisan.Bucket do
  use Writisan.Web, :model

  schema "buckets" do
    has_many :documents, Writisan.Document

    field :name, :string

    timestamps
  end

  @required_fields ~w()
  @optional_fields ~w()

  def changeset(model, params \\ :empty) do
    model
    |> cast(params, @required_fields, @optional_fields)
  end
end

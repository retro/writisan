defmodule Writisan.Comment do
  use Writisan.Web, :model

  schema "comments" do
    belongs_to :author, Writisan.User
    belongs_to :document, Writisan.Document

    field :content, :string
    field :idx, :integer
    field :subpath, :map

    timestamps
  end

  @required_fields ~w(author_id document_id content idx)
  @optional_fields ~w(subpath)

  def changeset(model, params \\ :empty) do
    model
    |> cast(params, @required_fields, @optional_fields)
  end
end

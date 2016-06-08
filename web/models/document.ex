defmodule Writisan.Document do
  use Writisan.Web, :model

  schema "documents" do
    belongs_to :author, Writisan.User
    has_many :doc_shares, Writisan.DocShare
    has_many :reviewers, through: [:doc_shares, :user]

    field :hash, :string
    field :content, :string
    field :parts, {:array, :string}

    timestamps
  end

  @required_fields ~w(content parts)
  @optional_fields ~w()

  def changeset(model, params \\ :empty) do
    model
    |> cast(params, @required_fields, @optional_fields)
    |> unique_constraint(:hash)
  end
end

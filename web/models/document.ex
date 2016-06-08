defmodule Writisan.Document do
  use Writisan.Web, :model

  schema "documents" do
    belongs_to :author, Writisan.User
    belongs_to :bucket, Writisan.Bucket
    belongs_to :prev_version, Writisan.Document
    has_one :next_version, Writisan.Document, foreign_key: :prev_version_id
    has_many :comments, Writisan.Comment
    has_many :doc_shares, Writisan.DocShare
    has_many :reviewers, through: [:doc_shares, :user]

    field :hash, :string
    field :content, :string
    field :parts, {:array, :string}

    timestamps
  end

  @required_fields ~w(author_id bucket_id hash content parts)
  @optional_fields ~w(prev_version_id)

  def changeset(model, params \\ :empty) do
    model
    |> cast(params, @required_fields, @optional_fields)
    |> unique_constraint(:hash)
  end
end

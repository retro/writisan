defmodule Writisan.User do
  use Writisan.Web, :model

  schema "users" do
    has_many :documents, Writisan.Document, foreign_key: :author_id
    has_many :comments, Writisan.Comment, foreign_key: :author_id

    field :uid, :string
    field :token, :string
    field :name, :string
    field :email, :string
    field :avatar, :string
    field :refresh_token, :string
    field :token_expires_at, Ecto.DateTime

    timestamps
  end

  @required_fields ~w(uid token)
  @optional_fields ~w(name email token_expires_at)

  def changeset(model, params \\ :empty) do
    model
    |> cast(params, @required_fields, @optional_fields)
    |> validate_format(:email, ~r/@/)
    |> validate_length(:email, min: 1, max: 100)
    |> validate_length(:name, min: 1, max: 50)
    |> unique_constraint(:uid)
  end
end


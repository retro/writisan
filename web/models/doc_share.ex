defmodule Writisan.DocShare do
  use Writisan.Web, :model

  schema "documents" do
    belongs_to :user, Writisan.User
    belongs_to :document, Writisan.Document
    field :role, :string
    
    timestamps
  end

  @required_fields ~w(user_id, document_id)
  @optional_fields ~w()

  def changeset(model, params \\ :empty) do
    model
    |> cast(params, @required_fields, @optional_fields)
  end
end

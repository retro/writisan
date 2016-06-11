defmodule Writisan.API.V1.DocumentController do
  use Writisan.Web, :controller
  use Guardian.Phoenix.Controller

  alias Writisan.Document
  alias Writisan.Bucket
  import Ecto.Query

  plug :scrub_params, "document" when action in [:create, :update]

  def index(conn, params, user, claims) do
    user = Repo.preload(user, :documents)
    documents = Repo.preload(user.documents, :comments)

    render(conn, "index.json", documents: documents)
  end

  def show(conn, %{"id" => id}, user, claims) do
    document = Repo.get!(Document, id)
    |> Repo.preload(:comments)
    |> Repo.preload([prev_version: :comments])
    |> Repo.preload([next_version: :comments])

    render(conn, "show.json", document: document)
  end

  def create(conn, %{"document" => document_params}, user, claims) do
    data = Map.merge(document_params, %{
      "author_id" => user.id,
      "bucket_id" => determine_bucket_id(document_params),
      "parts" => to_parts(document_params),
      "hash" => hash_from_content(document_params)
    })

    changeset = Document.changeset(%Document{}, data)

    case Repo.insert(changeset) do
      {:ok, document} ->
        conn
        |> put_status(:created)
        |> render("show.json", document: document)
      {:error, changeset} ->
        conn
        |> put_status(:unprocessable_entity)
        |> render(Writisan.ChangesetView, "error.json", changeset: changeset)
    end
  end

  def delete(conn, %{"id" => id}) do
    document = Repo.get!(Document, id)
    Repo.delete!(document)
    send_resp(conn, :no_content, "")
  end

  def to_parts(%{"content" => content} = doc_params) do
    Earmark.to_html(content)
    |> String.split("__________CUTHERE__________")
  end

  def hash_from_content(%{"content" => content} = doc_params) do
    FNV.FNV1a.hex128(content)
  end

  def determine_bucket_id(%{"bucket_id" => bucket_id} = doc_params) do
    bucket_id
  end

  def determine_bucket_id(doc_params) do
    cs = Bucket.changeset(%Bucket{}, %{})
    Repo.insert!(cs).id
  end
end

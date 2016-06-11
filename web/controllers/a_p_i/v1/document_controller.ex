defmodule Writisan.API.V1.DocumentController do
  use Writisan.Web, :controller
  use Guardian.Phoenix.Controller

  alias Writisan.API.V1.DocumentView
  alias Writisan.Endpoint
  alias Writisan.Document
  alias Writisan.User
  alias Writisan.Bucket
  import Ecto.Query

  plug :scrub_params, "document" when action in [:create, :update]

  def index(conn, %{"document_hash" => hash} = params, user, claims), do: show(conn, params, user, claims)
  def index(conn, params, user, claims) do
    documents =
      Document
      |> join(:left, [d], s in assoc(d, :shares))
      |> where([d, s], d.author_id == ^user.id or s.user_id == ^user.id)
      |> Repo.all
      |> Repo.preload([:author, :prev_version, :next_version])

    render(conn, "index.json", documents: documents)
  end

  def show(conn, %{"document_hash" => hash}, user, claims) do
    document =
      Document
      |> Repo.get_by!(hash: hash)
      |> Repo.preload(:author)
      |> Repo.preload(:prev_version)
      |> Repo.preload(:next_version)

    render(conn, "show.json", document: document)
  end

  def create(conn, %{"document" => document_params}, user, claims) do
    data = Map.merge(document_params, %{
      "author_id" => user.id,
      "bucket_id" => determine_bucket_id(document_params),
      "prev_version_id" => determine_prev_version_id(document_params),
      "parts" => content_to_parts(document_params),
      "hash" => content_to_hash(document_params)
    })

    changeset = Document.changeset(%Document{}, data)

    case Repo.insert(changeset) do
      {:ok, document} ->

        document = document |> Repo.preload([:author, :next_version, :prev_version])
        data = DocumentView.render("show.json", %{document: document})
        Endpoint.broadcast! "data:documents", "new_document", data

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

  def content_to_parts(%{"content" => content} = doc_params) do
    Earmark.to_html(content)
    |> String.split("__________CUTHERE__________")
  end

  def content_to_hash(%{"content" => content} = doc_params) do
    FNV.FNV1a.hex256(content)
  end

  def determine_prev_version_id(%{"prev_version_hash" => prev_version_hash} = doc_params) do
    case Repo.get_by(Document, hash: prev_version_hash) do
      nil -> nil
      %{id: id, hash: hash} -> id
    end
  end
  def determine_prev_version_id(doc_params), do: nil

  def determine_bucket_id(%{"bucket_id" => bucket_id} = doc_params), do: bucket_id
  def determine_bucket_id(doc_params) do
    Repo.insert!(Bucket.changeset(%Bucket{}, %{})).id
  end
end

defmodule Writisan.API.DocumentController do
  use Writisan.Web, :controller
  use Guardian.Phoenix.Controller

  alias Writisan.Document

  plug :scrub_params, "document" when action in [:create, :update]

  def index(conn, params, user, claims) do
    documents = Repo.all(Document)
    render(conn, "index.json", documents: documents)
  end

  def create(conn, %{"document" => document_params}) do
    changeset = Document.changeset(%Document{}, document_params)

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

  def show(conn, %{"id" => id}) do
    document = Repo.get!(Document, id)
    render(conn, "show.json", document: document)
  end

  def update(conn, %{"id" => id, "document" => document_params}) do
    document = Repo.get!(Document, id)
    changeset = Document.changeset(document, document_params)

    case Repo.update(changeset) do
      {:ok, document} ->
        render(conn, "show.json", document: document)
      {:error, changeset} ->
        conn
        |> put_status(:unprocessable_entity)
        |> render(Writisan.ChangesetView, "error.json", changeset: changeset)
    end
  end

  def delete(conn, %{"id" => id}) do
    document = Repo.get!(Document, id)

    # Here we use delete! (with a bang) because we expect
    # it to always work (and if it does not, it will raise).
    Repo.delete!(document)

    send_resp(conn, :no_content, "")
  end
end

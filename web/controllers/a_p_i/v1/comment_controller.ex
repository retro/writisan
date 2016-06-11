defmodule Writisan.API.V1.CommentController do
  use Writisan.Web, :controller
  use Guardian.Phoenix.Controller

  alias Writisan.Comment
  alias Writisan.Document
  import Ecto.Query

  plug :scrub_params, "comment" when action in [:create, :update]

  def index(conn, %{"document_hash" => hash}, user, claims) do
    comments = Comment
    |> join(:inner, [c], d in Document, c.document_id == d.id)
    |> where([c, d], d.hash == ^hash)
    |> Repo.all

    render(conn, "index.json", comments: comments)
  end

  def index(conn, _params, user, claims) do
    user = Repo.preload(user, :comments)
    comments = Repo.preload(user.comments, :document)

    render(conn, "index.json", comments: comments)
  end


  def show(conn, %{"id" => id}, user, claims) do
    comment = Repo.get!(Comment, id)
    |> Repo.preload(:document)

    render(conn, "show.json", comment: comment)
  end

  def create(conn, %{"comment" => comment_params}, user, claims) do
    data = Map.merge(comment_params, %{
      "author_id" => user.id
    })

    changeset = Comment.changeset(%Comment{}, data)

    case Repo.insert(changeset) do
      {:ok, comment} ->
        comment = comment |> Repo.preload(:document)
        conn
        |> put_status(:created)
        |> render("show.json", comment: comment)
      {:error, changeset} ->
        conn
        |> put_status(:unprocessable_entity)
        |> render(Writisan.ChangesetView, "error.json", changeset: changeset)
    end
  end

  def delete(conn, %{"id" => id}) do
    comment = Repo.get!(Comment, id)

    # Here we use delete! (with a bang) because we expect
    # it to always work (and if it does not, it will raise).
    Repo.delete!(comment)

    send_resp(conn, :no_content, "")
  end
end

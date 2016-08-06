defmodule Writisan.API.V1.CommentController do
  use Writisan.Web, :controller
  use Guardian.Phoenix.Controller

  alias Writisan.API.V1.CommentView
  alias Writisan.Endpoint
  alias Writisan.Comment
  alias Writisan.Document
  alias Writisan.Share
  import Ecto.Query

  plug :scrub_params, "comment" when action in [:create, :update]

  def index(conn, %{"document_hash" => hash}, user, claims) do
    comments = Comment
    |> join(:inner, [c], d in assoc(c, :document))
    |> join(:inner, [c, d], a in assoc(c, :author))
    |> join(:left, [c, d, a], s in assoc(d, :shares))
    |> where([c, d, a, s], d.hash == ^hash)
    |> where([c, d, a, s], d.author_id == ^user.id or s.user_id == ^user.id)
    |> preload([c, d, a, s], [comment: :author])
    |> distinct(true)
    |> Repo.all

    render(conn, "index.json", comments: comments)
  end

  def index(conn, user, claims) do
    comments = Comment
    |> join(:inner, [c], d in assoc(c, :document))
    |> join(:inner, [c, d], a in assoc(c, :author))
    |> join(:left, [c, d, a], s in assoc(d, :share))
    |> where([c, d, a, s], s.user_id == ^user.id)
    |> Repo.all

    render(conn, "index.json", comments: comments)
  end

  def index(conn, _params, user, claims) do
    comments =
      Comment
      |> join(:inner, [c], a in assoc(c, :author))
      |> where([c, a], c.author_id == ^user.id)
      |> preload([c, a], :author)
      |> Repo.all

    render(conn, "index.json", comments: comments)
  end

  def create(conn, %{"comment" => comment_params}, user, claims) do
    data = Map.merge(comment_params, %{
      "document_id" => determine_document_id(comment_params),
      "uid" => UUID.uuid1,
      "author_id" => user.id
    })

    changeset = Comment.changeset(%Comment{}, data)

    case Repo.insert(changeset) do
      {:ok, comment} ->
        comment = Repo.preload(comment, :author)
        data = CommentView.render("show.json", %{comment: comment})
        Endpoint.broadcast! "data:comments", "new_comment", data

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

  def determine_document_id(%{"document_hash" => doc_hash} = params) do
    case Repo.get_by(Document, hash: doc_hash) do
      nil -> nil
      %{id: id, hash: hash} -> id
    end
  end

  def determine_document_id(params), do: -1
end

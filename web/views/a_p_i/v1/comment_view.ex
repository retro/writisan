defmodule Writisan.API.V1.CommentView do
  use Writisan.Web, :view
  alias Writisan.Document

  def render("index.json", %{comments: comments}) do
    %{data: render_many(comments, Writisan.API.V1.CommentView, "comment.json")}
  end

  def render("show.json", %{comment: comment}) do
    %{data: render_one(comment, Writisan.API.V1.CommentView, "comment.json")}
  end

  def render("comment.json", %{comment: comment}) do
    %{
      uid: comment.uid,
      idx: comment.idx,
      content: comment.content,
      subpath: comment.subpath,
      author: author(comment)
    }
  end

  defp author(comment) do
    %{
      uid: comment.author.uid,
      name: comment.author.name,
      email: comment.author.email
    }
  end
end

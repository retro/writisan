defmodule Writisan.API.CommentView do
  use Writisan.Web, :view

  def render("index.json", %{comments: comments}) do
    %{data: render_many(comments, Writisan.API.CommentView, "comment.json")}
  end

  def render("show.json", %{comment: comment}) do
    %{data: render_one(comment, Writisan.API.CommentView, "comment.json")}
  end

  def render("comment.json", %{comment: comment}) do
    %{id: comment.id}
  end
end

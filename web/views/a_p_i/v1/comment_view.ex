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
      id: comment.id,
      idx: comment.idx,
      content: comment.content,
      subpath: comment.subpath,
      document: comment_doc(comment.document)
    }
  end

  defp comment_doc(%Document{} = doc) do
    %{
      id: doc.id,
      hash: doc.hash,
      content: doc.content,
      parts: doc.parts,
      author_id: doc.author_id
    }
  end

  defp comment_doc(_) do
    nil
  end
end

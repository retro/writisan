defmodule Writisan.API.V1.DocumentView do
  use Writisan.Web, :view
  alias Writisan.Document

  def render("index.json", %{documents: documents}) do
    %{data: render_many(documents, Writisan.API.V1.DocumentView, "document.json")}
  end

  def render("show.json", %{document: document}) do
    %{data: render_one(document, Writisan.API.V1.DocumentView, "document.json")}
  end

  def render("document.json", %{document: document}) do
    %{
      id: document.id,
      hash: document.hash,
      bucket_id: document.bucket_id,
      author_id: document.author_id,
      prev_version: doc_ver(document.prev_version),
      next_version: doc_ver(document.next_version),
      parts: document.parts,
      content: document.content,
      comments: doc_comments(document.comments)
    }
  end

  def doc_comments(comments) when is_list(comments) do
    Enum.map comments, fn(c) ->
      %{
        id: c.id,
        idx: c.idx,
        content: c.content
      }
    end
  end

  def doc_comments(comments) do
    []
  end

  def doc_ver(%Document{} = document) do
    %{
      id: document.id,
      hash: document.hash,
      content: document.content,
      parts: document.parts,
      comments: doc_comments(document.comments)
    }
  end

  def doc_ver(_) do
    nil
  end
end

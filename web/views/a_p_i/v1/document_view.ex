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
      hash: document.hash,
      parts: document.parts,
      content: document.content,
      author_uid: document.author && document.author.uid,
      prev_version_hash: document.prev_version && document.prev_version.hash,
      next_version_hash: document.next_version && document.next_version.hash
    }
  end
end

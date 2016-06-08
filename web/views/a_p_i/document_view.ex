defmodule Writisan.API.DocumentView do
  use Writisan.Web, :view

  def render("index.json", %{documents: documents}) do
    %{data: render_many(documents, Writisan.API.DocumentView, "document.json")}
  end

  def render("show.json", %{document: document}) do
    %{data: render_one(document, Writisan.API.DocumentView, "document.json")}
  end

  def render("document.json", %{document: document}) do
    %{id: document.id}
  end
end

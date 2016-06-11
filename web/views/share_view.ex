defmodule Writisan.ShareView do
  use Writisan.Web, :view

  def render("index.json", %{shares: shares}) do
    %{data: render_many(shares, Writisan.ShareView, "share.json")}
  end

  def render("show.json", %{share: share}) do
    %{data: render_one(share, Writisan.ShareView, "share.json")}
  end

  def render("share.json", %{share: share}) do
    %{
      id: share.id,
      user_id: share.user_id,
      document_id: share.document_id,
      role: share.role
    }
  end
end

defmodule Writisan.Handlers.BrowserAuth do
  def unauthenticated(conn, _params) do
    conn
    |> Phoenix.Controller.redirect(to: "/auth/google")
  end
end

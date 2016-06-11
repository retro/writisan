defmodule Writisan.PageController do
  use Writisan.Web, :controller
  use Guardian.Phoenix.Controller, only: [:app]

  plug Guardian.Plug.EnsureAuthenticated when action in [:app]

  def landing(conn, _params, user, claims), do: landing(conn, _params)
  def landing(conn, _params) do
    conn
    |> put_layout(false)
    |> render("landing.html")
  end

  def share(conn, %{"hash" => hash} = params, user, claims) do
    conn
    |> redirect(to: "/auth/google?state=#{hash}")
  end

  def app(conn, params, user, claims) do
    {:ok, jwt, full_claims} = Guardian.encode_and_sign(user, :api)

    conn
    |> put_layout(false)
    |> render("app.html", token: jwt)
  end
end


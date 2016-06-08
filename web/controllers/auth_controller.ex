defmodule Writisan.AuthController do
  use Writisan.Web, :controller

  plug Ueberauth

  alias Ueberauth.Strategy.Helpers
  alias Writisan.UserFromAuth

  def request(conn, _params) do
    IO.inspect conn
  end

  def callback(%{assigns: %{ueberauth_failure: _fails}} = conn, _params) do
    conn
    |> put_flash(:error, "Failed to authenticate.")
    |> redirect(to: "/")
  end

  def callback(%{assigns: %{ueberauth_auth: auth}} = conn, params) do
    case UserFromAuth.find_or_create(auth) do
      {:ok, user} ->
         conn
        |> Guardian.Plug.sign_in(user)
        |> put_flash(:info, "Logged in.")
        |> redirect(to: "/app")
      {:error, reason} ->
        conn
        |> put_flash(:error, reason)
        |> redirect(to: "/")
    end
  end

  def delete(conn, _params) do
    conn
    |> Guardian.Plug.sign_out
    |> put_flash(:info, "You have been logged out.")
    |> redirect(to: "/")
  end
end

defmodule Writisan.AuthController do
  use Writisan.Web, :controller

  plug Ueberauth

  alias Ueberauth.Strategy.Helpers
  alias Writisan.UserFromAuth

  alias Writisan.Share
  alias Writisan.Document

  def request(conn, _params) do
    # NOTHING TO DO
  end

  def callback(%{assigns: %{ueberauth_failure: _fails}} = conn, _params) do
    conn
    |> put_flash(:error, "Failed to authenticate.")
    |> redirect(to: "/")
  end

  def callback(
    %{assigns: %{ueberauth_auth: auth}} = conn,
    %{"state" => doc_hash} = params) do

    case UserFromAuth.find_or_create(auth) do
      {:ok, user} ->
        case Repo.get_by(Document, hash: doc_hash) do

          nil ->
            conn
            |> Guardian.Plug.sign_in(user)
            |> put_flash(:error, "no such document")
            |> redirect(to: "/app")

          %{id: doc_id, hash: doc_hash} ->
            data = %{"user_id" => user.id, "document_id" => doc_id}
            case Repo.insert(Share.changeset(%Share{}, data)) do
              {:ok, share} ->
                conn
                |> Guardian.Plug.sign_in(user)
                |> redirect(to: "/app?document_hash=#{doc_hash}")

              {:error, %{errors: errors} = changeset} ->
                conn
                |> Guardian.Plug.sign_in(user)
                |> put_flash(:error, concat_errors(errors))
                |> redirect(to: "/app")
            end
        end

      {:error, reason} ->
        conn
        |> put_flash(:error, reason)
        |> redirect(to: "/")
    end
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

  defp concat_errors(errors) do
    for {field, msg} <- errors, into: "", do: "#{msg} "
  end
end

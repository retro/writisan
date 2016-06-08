defmodule Writisan.UserFromAuth do
  alias Writisan.User
  alias Ueberauth.Auth
  alias Writisan.Repo

  import Writisan.UnixTsConvert

  def find_or_create(%Auth{} = auth) do
    if user = Repo.get_by(User, uid: auth.uid) do
      {:ok, auth |> update_user(user)}
    else
      {:ok, auth |> insert_user}
    end
  end

  defp basic_info(%User{} = user) do
    %{ id: user.id, uid: user.uid, name: user.name, avatar: user.avatar, token: user.token}
  end

  defp basic_info(%Auth{} = auth) do
    %{ uid: auth.uid, name: name_from_auth(auth), avatar: auth.info.image }
  end

  def insert_user(%Auth{} = auth) do
    expires_at =
      auth.credentials.expires_at
      |> from_timestamp
      |> Ecto.DateTime.from_erl

    data = %{
      email: auth.info.email,
      name: name_from_auth(auth),
      uid: auth.uid,
      token: auth.credentials.token,
      refresh_token: auth.credentials.refresh_token,
      token_expires_at: expires_at
    }

    %User{}
    |> User.changeset(data)
    |> Repo.insert!()
  end

  def update_user(%Auth{} = auth, %User{} = user) do
    expires_at =
      auth.credentials.expires_at
      |> from_timestamp
      |> Ecto.DateTime.from_erl

    changeset = User.changeset(user, %{
      email: auth.info.email,
      name: name_from_auth(auth),
      uid: auth.uid,
      token: auth.credentials.token,
      refresh_token: auth.credentials.refresh_token,
      token_expires_at: expires_at
    })

    changeset |> Repo.update!()
  end

  defp name_from_auth(auth) do
    if auth.info.name do
      auth.info.name
    else
      name = [auth.info.first_name, auth.info.last_name]
      |> Enum.filter(&(&1 != nil and &1 != ""))

      cond do
        length(name) == 0 -> auth.info.nickname
        true -> Enum.join(name, " ")
      end
    end
  end

  defp validate_pass(%{other: %{password: ""}}) do
    {:error, "Password required"}
  end
  defp validate_pass(%{other: %{password: pw, password_confirmation: pw}}) do
    :ok
  end
  defp validate_pass(%{other: %{password: _}}) do
    {:error, "Passwords do not match"}
  end
  defp validate_pass(_), do: {:error, "Password Required"}
end


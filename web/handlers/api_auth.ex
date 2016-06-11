defmodule Writisan.Handlers.ApiAuth do
  def unauthenticated(conn, _params) do
    conn
    |> Plug.Conn.put_status(401)
    |> Phoenix.Controller.json(%{message: "Get outta here!"})
  end
end

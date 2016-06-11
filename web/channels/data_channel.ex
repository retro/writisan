defmodule Writisan.DataChannel do
  use Phoenix.Channel

  def join("data:" <> topic, message, socket) do
    {:ok, %{message: "joined #{topic} channel"}, socket}
  end
end

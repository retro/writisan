defmodule Writisan.UserSocket do
  use Phoenix.Socket

  ## Channels
  channel "data:*", Writisan.DataChannel
  channel "data:documents", Writisan.DataChannel
  channel "data:comments", Writisan.DataChannel

  ## Transports
  transport :websocket, Phoenix.Transports.WebSocket, timeout: 45_000

  # transport :longpoll, Phoenix.Transports.LongPoll

  def connect(_params, socket) do
    {:ok, socket}
  end

  def id(_socket), do: nil
end

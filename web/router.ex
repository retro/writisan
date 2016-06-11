defmodule Writisan.Router do
  use Writisan.Web, :router

  pipeline :browser do
    plug :accepts, ["html"]
    plug :fetch_session
    plug :fetch_flash
    plug :protect_from_forgery
    plug :put_secure_browser_headers
  end

  pipeline :api do
    plug :put_headers, %{
      "Accept" => "application/json",
      "Content-Type" => "application/json"
    }
  end

  pipeline :browser_session do
    plug Guardian.Plug.VerifySession
    plug Guardian.Plug.EnsureAuthenticated,
      handler: Writisan.Handlers.BrowserAuth
    plug Guardian.Plug.LoadResource
  end

  pipeline :api_session do
    plug Guardian.Plug.VerifyHeader
    plug Guardian.Plug.EnsureAuthenticated,
      handler: Writisan.Handlers.ApiAuth
    plug Guardian.Plug.LoadResource
  end

  scope "/", Writisan do
    pipe_through :browser

    get "/", PageController, :landing
    get "/shares/:hash", PageController, :share

    scope "/app" do
      pipe_through [:browser, :browser_session]
      get "/", PageController, :app
    end
  end

  scope "/api", Writisan.API do
    pipe_through [:api, :api_session]

    scope "/v1", V1, as: :v1 do
      get "/documents/:hash", DocumentController, :show
      resources "/documents", DocumentController, except: [:show, :update]
      resources "/comments", CommentController
    end
  end

  scope "/auth", Writisan do
    pipe_through :browser

    get "/:provider", AuthController, :request
    get "/:provider/callback", AuthController, :callback
    post "/:provider/callback", AuthController, :callback
    delete "/logout", AuthController, :delete
  end

  defp put_headers(%Plug.Conn{} = conn, %{} = headers) do
    Enum.reduce headers, conn, fn {k, v}, conn ->
      Plug.Conn.put_resp_header(conn, k, v)
    end
  end
end

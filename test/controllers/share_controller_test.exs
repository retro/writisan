defmodule Writisan.ShareControllerTest do
  use Writisan.ConnCase

  alias Writisan.Share
  @valid_attrs %{}
  @invalid_attrs %{}

  setup %{conn: conn} do
    {:ok, conn: put_req_header(conn, "accept", "application/json")}
  end

  test "lists all entries on index", %{conn: conn} do
    conn = get conn, share_path(conn, :index)
    assert json_response(conn, 200)["data"] == []
  end

  test "shows chosen resource", %{conn: conn} do
    share = Repo.insert! %Share{}
    conn = get conn, share_path(conn, :show, share)
    assert json_response(conn, 200)["data"] == %{"id" => share.id}
  end

  test "does not show resource and instead throw error when id is nonexistent", %{conn: conn} do
    assert_error_sent 404, fn ->
      get conn, share_path(conn, :show, -1)
    end
  end

  test "creates and renders resource when data is valid", %{conn: conn} do
    conn = post conn, share_path(conn, :create), share: @valid_attrs
    assert json_response(conn, 201)["data"]["id"]
    assert Repo.get_by(Share, @valid_attrs)
  end

  test "does not create resource and renders errors when data is invalid", %{conn: conn} do
    conn = post conn, share_path(conn, :create), share: @invalid_attrs
    assert json_response(conn, 422)["errors"] != %{}
  end

  test "updates and renders chosen resource when data is valid", %{conn: conn} do
    share = Repo.insert! %Share{}
    conn = put conn, share_path(conn, :update, share), share: @valid_attrs
    assert json_response(conn, 200)["data"]["id"]
    assert Repo.get_by(Share, @valid_attrs)
  end

  test "does not update chosen resource and renders errors when data is invalid", %{conn: conn} do
    share = Repo.insert! %Share{}
    conn = put conn, share_path(conn, :update, share), share: @invalid_attrs
    assert json_response(conn, 422)["errors"] != %{}
  end

  test "deletes chosen resource", %{conn: conn} do
    share = Repo.insert! %Share{}
    conn = delete conn, share_path(conn, :delete, share)
    assert response(conn, 204)
    refute Repo.get(Share, share.id)
  end
end

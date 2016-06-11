defmodule Writisan.ShareTest do
  use Writisan.ModelCase

  alias Writisan.Share

  @valid_attrs %{}
  @invalid_attrs %{}

  test "changeset with valid attributes" do
    changeset = Share.changeset(%Share{}, @valid_attrs)
    assert changeset.valid?
  end

  test "changeset with invalid attributes" do
    changeset = Share.changeset(%Share{}, @invalid_attrs)
    refute changeset.valid?
  end
end

defmodule Writisan.Diff do
  require Logger

  @file_old "/tmp/old"
  @file_new "/tmp/new"

  def diff(old, new, type \\ :html) do
    with {:ok, diff} <- git_diff(old, new) do
      diff
      |> delete_hunks
      |> format(type)
      |> strip_surrounding_linebreaks
    end
    |> case do
      {:error, msg} -> returning(nil, fn ->
        Logger.error "Something happen while diffing:"
        Logger.error msg
      end)
      val -> val
    end
  end

  def git_diff(old, new) do
    with {:ok, file1} <- File.open(@file_old, [:write]),
    {:ok, file2} <- File.open(@file_new, [:write]),
    :ok <- IO.binwrite(file1, old),
    :ok <- IO.binwrite(file2, new),
    :ok <- File.close(file1),
    :ok <- File.close(file2) do
      case System.cmd("git", ~w(diff --no-index --word-diff --minimal --patience -- #{@file_old} #{@file_new})) do
        {diff, 1} -> {:ok, diff}
        {_, 0}    -> {:error, "Error: something happen while diffing!"}
      end
    end
  end

  defp format(string, :html) do
    string
    |> replace_ins_open
    |> replace_ins_close
    |> replace_del_open
    |> replace_del_close
  end

  defp format(string, :git) do
    string
  end

  defp delete_hunks(string) do
    Regex.replace(~r/(\@\@|---|\+\+\+|index|diff)\s.+$/mx, string, "")
  end

  defp strip_surrounding_linebreaks(string) do
    string
    |> String.lstrip
    |> String.rstrip
  end

  defp replace_ins_open(string) do
    Regex.replace(~r/\{\+/, string, "<ins>")
  end

  defp replace_ins_close(string) do
    Regex.replace(~r/\+\}/, string, "</ins>")
  end

  defp replace_del_open(string) do
    Regex.replace(~r/\[-/, string, "<del>")
  end

  defp replace_del_close(string) do
    Regex.replace(~r/-\]/, string, "</del>")
  end

  def returning(val, f) do
    f.()
    val
  end
end

import "deps/phoenix_html/web/static/js/phoenix_html"
import {Socket} from "deps/phoenix/web/static/js/phoenix"

let socket = new Socket("/socket", {params: {token: window.userToken}})
socket.connect()

// Now that you are connected, you can join channels with a topic:
let com_channel = socket.channel("data:comments", {})
let doc_channel = socket.channel("data:documents", {})

com_channel.join()
  .receive("ok", resp => { console.log(resp) })
  .receive("error", resp => { console.log(resp) })

doc_channel.join()
  .receive("ok", resp => { console.log(resp) })
  .receive("error", resp => { console.log(resp) })

com_channel.on("new_comment", function(resp) { console.log("COMMENT: ", resp) })
doc_channel.on("new_document", function(resp) { console.log("DOCUMENT: ", resp) })

export default socket

window.socket = socket;

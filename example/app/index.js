
// save me as my-app/index.js
var http = require('http')
var android = require('node-on-android')

var server = http.createServer(function (req, res) {
  res.end(`
    <html>
    <body>
      <h1>Welcome to Node.js hacking on Android</h1>
    </body>
    </html>
  `)
})

server.listen(0, function () {
  android.loadUrl(`http://localhost:${server.address().port}`)
})

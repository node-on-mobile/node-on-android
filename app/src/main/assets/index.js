var http = require('http')
var net = require('net')

var android = function () { // TODO: move to internal module
    var port = Number(process.argv[process.argv.length - 1])
    var sock = net.connect(port, '127.0.0.1')
    return {
        loadUrl: function (u) {
            sock.write(u)
        }
    }
}()

console.log("hello world")
console.log('argv', process.argv)

var server = http.createServer(function (req, res) {
    res.setHeader('Content-Type', 'text/html')
    res.end(`
        <html>
            <body>
                <h1>Hello</h1>
                <h1>From></h1>
                <h1>Node</h1>
            </body>
        </html>
    `)
})

server.listen(0, function () {
    android.loadUrl('http://localhost:' + server.address().port)
})
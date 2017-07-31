var http = require('http')
var cowsay = require("cowsay")
var android = require('node-on-android') // core

console.log(cowsay.say({
    text : "I'm a moooodule",
    e : "oO",
    T : "U "
}))

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

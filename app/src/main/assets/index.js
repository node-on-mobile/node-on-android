console.log("hello world")

var http = require('http')

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

server.listen(10000)
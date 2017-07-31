var net = require('net')

var android = function () {
    var port = Number(process.argv[process.argv.length - 1])
    var sock = net.connect(port, '127.0.0.1')
    return {
        loadUrl: function (u) {
            sock.write(u)
        }
    }
}()

module.exports = android
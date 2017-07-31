# Node on Android

Make Node.js apps for Android (Currently only supports ARM64)

## Installing

First install the command line tool from npm

``` sh
npm install -g node-on-android
```

You also need to fetch the Android SDK if you haven't (See "Get just the command line tools" [here](https://developer.android.com/studio/index.html))
and unpack them somewhere.

Then install `apktool` from brew or similar

``` sh
brew install apktool
```

That's it! You are now ready to write Node.js apps for Android.

## Building an app

Node on android works by running your Node.js inside the android app using a shared library.
It then bundles a WebView that hosts your UI code. All UI is just classic html/css/js.

In the node app you can require `node-on-android` to get access to the WebView.
You can use this to load an html page in the WebView

``` js
// in the node app
var android = require('node-on-android')

// will load localhost:1000 in the webview
android.loadUrl('http://localhost:10000')
```

You can call `loadUrl` as many times as you want. It'll just change the WebView address.

Here is an example app

``` js
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
```

To bundle up the Node.js app into an apk file use the command line tool

```
node-on-android ./my-app -o my-app.apk -b ./path/to/android/build/tools
```

If you installed Android Studio on Mac the build tools are usually installed in a path similar to `~/Library/Android/sdk/build-tools/26.0.1/`.

After the above succeds you should be able to install my-app.apk on your Android phone
and run the Node.js app.

Happy mobile hacking!

## License

MIT

# Node on Android

Make Node.js apps for Android (Currently only supports ARM64)

## Installing

First install the command line tool from npm

``` sh
npm install -g node-on-android
```

Or get it from git:

```
git clone https://github.com/node-on-mobile/node-on-android
cd cli/
npm i
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

## Instructions for GNU/Linux

To install `apktool` go to the [apktool website](https://ibotpeaches.github.io/Apktool/install/) and follow the installation guide for linux. Here it is in script form (but make sure you get the latest versions):

```
cd /tmp
wget https://raw.githubusercontent.com/iBotPeaches/Apktool/master/scripts/linux/apktool
wget https://bitbucket.org/iBotPeaches/apktool/downloads/apktool_2.2.4.jar
mv apktool_2.2.4.jar apktool.jar
chmod 755 apktool apktool.jar
sudo mv apktool apktool.jar /usr/local/bin/
```

In order to get the `appsigner` and `zipalign` commands you'll need to download the Android SDK tools and use the `sdkmanager` commmand.

On GNU/Linux you can download the sdk-tools package from the [android website](https://developer.android.com/studio/index.html), e.g. [sdk-tools-linux-3859397.zip](https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip) and extract it to e.g. /opt/android-sdk-tools

Then to install the required `appsigner` and `zipalign` tools first use the sdkmanager command to list available packages:

```
/opt/android-sdk-tools/bin/sdkmanager --list
```

Find the latest `build-tools` version listed and install it with e.g:

```
/opt/android-sdk-tools/bin/sdkmanager --sdk_root=/opt/android-sdk-tools 'build-tools;26.0.1'
```

Make sure you run the previous command as a user that has write access to your sdk directory (in this case `/opt/android-sdk-tools/bin`.

Now you should have `zipalign` and `apksigner` available in:

```
/opt/android-sdk-tools/build-tools/26.0.1
```

You can use this path as your `-b` argument for the `node-on-android` command but you should really put `zipalign` and `apksigner` in your path like so:

```
cd /usr/local/bin
sudo ln -s /opt/android-sdk-tools/build-tools/26.0.1/zipalign
sudo ln -s /opt/android-sdk-tools/build-tools/26.0.1/apksigner
```

## Example

There is an example app ready to try in the `example/` directory.

First ensure that the dependencies are installed:

```
cd cli/
npm i
cd ..
```

You will also need to change the `-b` argument in build command in `example/package.json` if you don't have symlinks to `zipalign` and `apksigner` in `/usr/local/bin`.

```
cd example/
npm run build
```

To send it to your phone, enable adb debug mode on your android device, connect it over USB and run:

```
adb install build/example.apk
```

The app will show up in your app list as "Node On Android". You can also launch it using:

```
adb shell
am start -n com.mafintosh.nodeonandroid/com.mafintosh.nodeonandroid.MainActivity
```

## License

MIT

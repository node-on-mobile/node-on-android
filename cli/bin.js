#!/usr/bin/env node

var os = require('os')
var path = require('path')
var mkdirp = require('mkdirp')
var proc = require('child_process')
var minimist = require('minimist')

var argv = minimist(process.argv.slice(2), {
  alias: {
    b: 'build-tools',
    o: 'out'
  },
  default: {
    o: 'app.apk'
  }
})

if (!argv.b) {
  console.error('--build-tools=[path/to/android/build/tools] is required')
  process.exit(1)
}

var cwd = argv._[0] || process.cwd()
var app = path.resolve(cwd, argv.o || 'app.apk')
var buildTools = argv.b
var base = path.join(__dirname, 'base.apk')
var tmp = path.join(os.tmpdir(), 'node-on-android-' + Date.now())
var node = path.join(tmp, 'base', 'assets', 'node')
var keystore = path.join(__dirname, 'whatever.keystore')

mkdirp.sync(tmp)

run('rm', ['-f', app])
run('apktool', ['d', base])
run('rm', ['-rf', node])
run('cp', ['-rf', cwd, node])
run('apktool', ['b', 'base', '-o', app])
run(path.join(buildTools, 'zipalign'), ['-v', '-p', '4', app, app + '.aligned'])
run('mv', [app + '.aligned', app])
run(path.join(buildTools, 'apksigner'), ['sign', '--ks-pass', 'pass:whatever', '--ks', keystore, '--out', app, app])
run('rm', ['-rf', tmp])

console.log('Done! apk file is stored in:')
console.log(app)

function run (cmd, args) {
  proc.spawnSync(cmd, args, {
    cwd: tmp,
    stdio: 'inherit'
  })
}

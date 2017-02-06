var webpack = require('webpack')
var path = require('path')

var CopyWebpackPlugin = require('copy-webpack-plugin');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var webpackUglifyJsPlugin = require('webpack-uglify-js-plugin');
var ngAnnotatePlugin = require('ng-annotate-webpack-plugin');

var env = process.env.NODE_ENV || 'development'
const __PROD__ = env === 'production'
const __DEV__  = env === 'development'

const webpackConfig = {
  watch: true,
  context: __dirname,
  entry: {
    app: './index.js',
    vendor: [
      'jquery',
      'angular',
      'angular-route',
      'bootstrap'
    ]
  },
  module: {}
}

if (__DEV__) {
  webpackConfig.entry.app = './index.dev.js'
}
if (__PROD__) {
  webpackConfig.watch = false
}

//Output
webpackConfig.output = {
  path: __dirname + '/build/',
  path: __dirname + '/build/assets/',
  filename: 'bank.bundle.js',
  publicPath: "./assets/"
}

//Plugins
webpackConfig.plugins = [
  new webpack.ProvidePlugin({
    $: "jquery",
    jQuery: "jquery"
  }),
  new webpack.optimize.CommonsChunkPlugin(/* chunkName= */"vendor", /* filename= */"vendor.bundle.js"),
  new ExtractTextPlugin("/styles/style.css"),
  new ngAnnotatePlugin({add: true}),
  new CopyWebpackPlugin([
    {
      from: './node_modules/bootstrap/dist/fonts',
      to: 'fonts'
    }
  ]),
]
if (__DEV__) {
  webpackConfig.plugins.push(new CopyWebpackPlugin([
    {
      from: './app/index.html',
      to: '../index.html'
    }
  ]))
}

if (__PROD__) {
  webpackConfig.plugins.push(
    new webpackUglifyJsPlugin({
      cacheFolder: path.resolve(__dirname, 'build/cached_uglify/'),
      debug: true,
      minimize: true,
      sourceMap: false,
      output: {
        comments: false
      },
      compressor: {
        warnings: false
      },
      options: {
        mangle: {
          except: ['jQuery', 'angular']
        }
      }
    })
  );
}

//Loaders
webpackConfig.module.loaders = [
  {
    test: /\.js$/,
    exclude: /(node_modules|bower_components)/,
    loader: 'babel-loader',
    query: {
      presets: ['es2015']
    }
  },
  {
    test: /\.css$/,
    loader: ExtractTextPlugin.extract("style-loader", "css-loader?-url", {publicPath: '../'}),
    options: {relativeUrls: false}
  },
  {
    test: /\.html$/,
    loader: "ng-cache?prefix=[dir]&-url"
  },
  {
    test: /\.(jp?g|png|gif|svg)$/i,
    loader: 'file'
  },
  {
    test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/font-woff'
  },
  {
    test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/octet-stream'
  },
  {
    test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: 'file'
  },
  {
    test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=image/svg+xml'
  }
]

module.exports = webpackConfig;

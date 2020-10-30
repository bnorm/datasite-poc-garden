// Allow fallback to root index.html file
if (config.devServer) {
    config.output.publicPath = "/";
    // config.devServer.host = "0.0.0.0"; // Makes devServer available on network
    config.devServer.publicPath = "/";
    config.devServer.historyApiFallback = true;
}

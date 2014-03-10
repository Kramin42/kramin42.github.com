// web.js
//var url = require("url");
var fs = require("fs");
var express = require("express");
var logfmt = require("logfmt");
var app = express();

var oneDay = 86400000;

app.use(logfmt.requestLogger());

//app.use(express.compress());

app.use(express.static(__dirname, { maxAge: oneDay }));

app.get('/', function(req, res) {
	res.send(fs.readFileSync('./index.htm').toString());
});

app.get('/fireworktext', function(req, res) {
  //var pathname = url.parse(request.url).pathname;
  //var pathname = request.params[0];
  //console.log("Request for " + pathname + " received.");
  //response.writeHead(200, {"Content-Type": "text/html"});
  //response.write(fs.readFileSync("./fireworks_text.htm").toString().replace("<INSERT_HERE>","\"Hello\","+"\""+pathname.split("/")[1]+"\""));
  //response.end();
  var text = '"'+req.query.text.split(' ').join('","')+'"';
  console.log(text);
  res.send(fs.readFileSync("./fireworks_text_template.htm").toString().replace("<INSERT_HERE>",text));
});

//app.get('/processing-1.4.1.min.js', function(req, res) {
//	res.set('Content-Type', 'text/plain');
//  	res.send(fs.readFileSync("./processing-1.4.1.min.js").toString());
//});

var port = Number(process.env.PORT || 5000);
app.listen(port, function() {
  console.log("Listening on " + port);
});
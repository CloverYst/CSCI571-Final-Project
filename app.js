var express = require('express');
var cors = require('express-cors');
var bodyParser = require('body-parser');
const { join, resolve } = require('path');
var app = express();
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(cors());

var request = require('request');
var request_promise = require('request-promise');
const { constants } = require('buffer');
const { rejects } = require('assert');
const { json } = require('body-parser');

app.all('*', function(req, res, next) {
	res.header('Access-Control-Allow-Origin', '*');
	res.header('Access-Control-Allow-Headers', 'Content-Type,Content-Length, Authorization, Accept,X-Requested-With');
	res.header('Access-Control-Allow-Methods', 'PUT,POST,GET,DELETE,OPTIONS');
	res.header('X-Powered-By', ' 3.2.1');
	if (req.method == 'OPTIONS') res.send(200); /*让options请求快速返回*/
	else next();
});

app.post('/ebay', function(req, res) {
	//res.send({id:req.params.id, name: req.params.password});
	data = req.body['data'];
	data = JSON.parse(data);
	get_data_from_ebay(data)
		.then((data) => {
			console.log('finish get data from ebay');
			console.log(data);
			return process_data(data);
		})
		.then((data) => {
			console.log('finish process data');
			console.log(data);
			res.send(data);
		});
});
app.get('/jsondata', function(req, res) {
	//res.send({id:req.params.id, name: req.params.password});
	var data = { key_word: 'iphone', conditon: [], return_accepted: [], shipping: [], sort_by: 'Best Match' };
	get_data_from_ebay(data).then((data) => {
		console.log('finish get data from ebay');
		data = JSON.parse(data);
		res.send(data);
	});
});
app.get('/test', function(req, res) {
	//res.send({id:req.params.id, name: req.params.password});
	res.send({ sb: 1 });
});

//start the server
const PORT = process.env.POORT || 8080;
app.listen(PORT, () => {
	console.log(`APP listening on port ${PORT}`);
	console.log();
});
module.exports = app;

function process_data(data) {
	data = JSON.parse(data);
	data = data['findItemsByKeywordsResponse'][0];
	ack = data['ack'][0];
	console.log(ack);
	if (ack == 'Failure') {
		data = {};
		data['count'] = 0;
		data['ack'] = 'F';
		return data;
	}
	count = data['searchResult'][0]['@count'];
	console.log(count);
	items = data['searchResult'][0]['item'];
	items_arr = [];
	i = 0;
	//console.log(items[0])
	console.log(items);
	if (items) {
		for (item of items) {
			item_json = {};

			try {
				itemID = item['itemId'][0];
				title = item['title'][0];
				location = item['location'];
				category_name = item['primaryCategory'][0]['categoryName'][0];
				img_url = item['galleryURL'][0];
				if (img_url == 'https://thumbs1.ebaystatic.com/pict/04040_0.jpg')
					img_url = 'https://csci571.com/hw/hw8/images/ebayDefault.png';

				item_url = item['viewItemURL'][0];
				conditon = item['condition'][0]['conditionDisplayName'][0];
				if ('false' in item['topRatedListing']) top_rating = false;
				else top_rating = true;
				item_price = parseFloat(item['sellingStatus'][0]['convertedCurrentPrice'][0]['__value__']);
				if ('false' in item['shippingInfo'][0]['shippingType']) free_ship = false;
				else free_ship = true;
				shippingType = item['shippingInfo'][0]['shippingType'][0];
				var ship_fee = 0;
				if (!free_ship)
					ship_fee = parseFloat(item['shippingInfo'][0]['shippingServiceCost'][0]['__value__'][0]);
				total_price = item_price + ship_fee;
				shippingCost = parseFloat(item['shippingInfo'][0]['shippingServiceCost'][0]['__value__']);
				shippingLocation = item['shippingInfo'][0]['shipToLocations'][0];
				expeditedShipping = item['shippingInfo'][0]['expeditedShipping'][0];
				oneDayShipping = item['shippingInfo'][0]['oneDayShippingAvailable'][0];
				BestOfferEnabled = item['listingInfo'][0]['bestOfferEnabled'][0];
				BuyItNowAvailable = item['listingInfo'][0]['buyItNowAvailable'][0];
				ListingType = item['listingInfo'][0]['listingType'][0];
				Gift = item['listingInfo'][0]['gift'][0];
				WatchCount = parseFloat(item['listingInfo'][0]['watchCount'][0]);
				handlingTime = item['shippingInfo'][0]['handlingTime'][0];
			} catch (error) {
				console.log(error);
				continue;
			}
			item_json['itemID'] = itemID;
			item_json['title'] = title;
			item_json['location'] = location;
			item_json['category_name'] = category_name;
			item_json['img_url'] = img_url;
			item_json['item_url'] = item_url;
			item_json['conditon'] = conditon;
			item_json['top_rating'] = top_rating;
			item_json['total_price'] = total_price;
			item_json['shippingType'] = shippingType;
			item_json['shippingCost'] = shippingCost;
			item_json['shippingToLocation'] = shippingLocation;
			item_json['expeditedShipping'] = expeditedShipping;
			item_json['oneDayShipping'] = oneDayShipping;
			item_json['BestOfferEnabled'] = BestOfferEnabled;
			item_json['BuyItNowAvailable'] = BuyItNowAvailable;
			item_json['ListingType'] = ListingType;
			item_json['Gift'] = Gift;
			item_json['WatchCount'] = WatchCount;
			item_json['handlingTime'] = handlingTime;
			items_arr.push(item_json);
			i += 1;
			if (i == 1000) break;
		}
	}

	data = {};
	data['count'] = i;
	data['ack'] = 'T';
	data['items'] = items_arr;
	console.log(data);
	return data;
}

function get_data_from_ebay(data) {
	// data is a json
	//console.log(data);
	var i = 0;
	var kw = data['key_word'];
	var url =
		'https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0\
&SECURITY-APPNAME=yunpengj-571-PRD-32eb84eea-4ab266de\
&RESPONSE-DATA-FORMAT=JSON\
&REST-PAYLOAD\
&paginationInput.entriesPerPage=1000\
&keywords=' +
		kw;

	if ('range_from' in data) {
		var lp = data['range_from'];
		url +=
			'&itemFilter(' +
			i +
			').name=MinPrice' +
			'&itemFilter(' +
			i +
			').value=' +
			lp +
			'&itemFilter(' +
			i +
			').paramName=Currency&itemFilter(' +
			i +
			').paramValue=USD';
		i += 1;
	}
	if ('range_to' in data) {
		var hp = data['range_to'];
		url +=
			'&itemFilter(' +
			i +
			').name=MaxPrice' +
			'&itemFilter(' +
			i +
			').value=' +
			hp +
			'&itemFilter(' +
			i +
			').paramName=Currency&itemFilter(' +
			i +
			').paramValue=USD';
		i += 1;
	}
	var cd = data['condition'];
	var j = 0;
	if (cd) {
		//cd != []:
		url += '&itemFilter(' + i + ').name=Condition';
		if ('New' in cd) {
			url += '&itemFilter(' + i + ').value(' + j + ')=New';
			j += 1;
		} else if ('Used' in cd) {
			url += '&itemFilter(' + i + ').value(' + j + ')=3000';
			j += 1;
		} else if ('Very Good' in cd) {
			url += '&itemFilter(' + i + ').value(' + j + ')=4000';
			j += 1;
		} else if ('Good' in cd) {
			url += '&itemFilter(' + i + ').value(' + j + ')=5000';
			j += 1;
		} else url += '&itemFilter(' + i + ').value(' + j + ')=6000';
		i += 1;
	}
	var seller = data['return_accepted'];
	if (seller) {
		url += '&itemFilter(' + i + ').name=ReturnsAcceptedOnly&itemFilter(' + i + ').value=True';
		i += 1;
	}
	var ship = data['shipping'];
	if (ship) {
		if ('Expedited' in ship) {
			url += '&itemFilter(' + i + '.name=ExpeditedShippingType&itemFilter(' + i + ').value=Expedited';
			i += 1;
		}
		if ('free' in ship) {
			url += '&itemFilter(' + i + '.name=FreeShippingOnly&itemFilter(' + i + ').value=True';
			i += 1;
		}
	}
	var sort = data['sort_by'];
	if (sort == 'Best Match') sort = 'BestMatch';
	else if (sort == 'Price: highest first') sort = 'CurrentPriceHighest';
	else if (sort == 'Price + Shipping: highest first') sort = 'PricePlusShippingHighest';
	else sort = 'PricePlusShippingLowest';

	url += '&sortOrder=' + sort;

	var promise = new Promise(function(resolve, reject) {
		request(url, function(err, res, body) {
			if (!err && res.statusCode == 200) {
				resolve(body);
			} else {
				reject(new Error(err));
			}
		});
	});
	return promise;
}

function get_singleitem(data) {
	var idd = data;
	var url =
		'http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=ShutangY-app-PRD-e2eba3d7e-e2362d14&siteid=0&version=967&ItemID=' +
		idd +
		'&IncludeSelector=Description,Details,ItemSpecifics';
	var promise = new Promise(function(resolve, reject) {
		request(url, function(err, res, body) {
			if (!err && res.statusCode == 200) {
				resolve(body);
			} else {
				reject(new Error(err));
			}
		});
	});
	return promise;
}

app.post('/details', function(req, res) {
	data = req.body['data'];
	/* req
    {
      "data": "67890"
    }
  */
	// id : stirng
	console.log('^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^');
	console.log(data);
	get_singleitem(data).then((data) => {
		//console.log(data);
		data = processdetail(data);
		res.send(data);
	});
});
function processdetail(data) {
	data = JSON.parse(data);
	data = data['Item'];
	try {
		var title = data['Title'];
		var subtitle = data['Subtitle'];
		var userID = data['Seller']['UserID'];
		var feedbackscore = data['Seller']['FeedbackScore'];
		var positivefb = data['Seller']['PositiveFeedbackPercent'];
		var fdratingstar = data['Seller']['FeedbackRatingStar'];
		var refund = data['ReturnPolicy']['Refund'];
		var rWithin = data['ReturnPolicy']['ReturnsWithin'];
		var SCpaidby = data['ReturnPolicy']['ShippingCostPaidBy'];
		var rAccpet = data['ReturnPolicy']['ReturnsAccepted'];
		var pictureURL = data['PictureURL'];
		var brand = data['ItemSpecifics']['NameValueList'][0]['Value'][0];
		var spec1=data['ItemSpecifics']['NameValueList'][1]['Name'];
		spec1=spec1+':'+data['ItemSpecifics']['NameValueList'][1]['Value'][0];
		var spec2=data['ItemSpecifics']['NameValueList'][2]['Name'];
		spec2=spec2+':'+data['ItemSpecifics']['NameValueList'][2]['Value'][0];
		var spec3=data['ItemSpecifics']['NameValueList'][3]['Name'];
		spec3=spec3+':'+data['ItemSpecifics']['NameValueList'][3]['Value'][0];
		var spec4=data['ItemSpecifics']['NameValueList'][4]['Name'];
		spec4=spec4+':'+data['ItemSpecifics']['NameValueList'][4]['Value'][0];
		var spec5=data['ItemSpecifics']['NameValueList'][5]['Name'];
		spec5=spec5+':'+data['ItemSpecifics']['NameValueList'][5]['Value'][0];
	} catch (error) {
		console.log(error);
	}
	data = {};
	data['title'] = subtitle;
	data['userID'] = userID;
	data['feedbackscore'] = feedbackscore;
	data['positivefb'] = positivefb;
	data['fdratingstar'] = fdratingstar;
	data['refund'] = refund;
	data['rWithin'] = rWithin;
	data['SCpaidby'] = SCpaidby;
	data['rAccpet'] = rAccpet;
	data['pictureURL'] = pictureURL;
	data['brand'] = brand;
	data['spec1']= spec1;
	data['spec2']= spec2;
	data['spec3']= spec3;
	data['spec4']= spec4;
	data['spec5']= spec5;
	console.log('^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^');
	console.log('finish process details');
	console.log(spec1);
	console.log(data);
	return data;
}
app.get('/singleitem', function(req, res) {
	//res.send({id:req.params.id, name: req.params.password});
	var data = "253462854618";
	get_singleitem(data).then((data) => {
		console.log('finish get data from ebay');
		data = JSON.parse(data);
		res.send(data);
	});
});

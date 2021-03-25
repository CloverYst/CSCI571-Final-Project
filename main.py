from flask import Flask, jsonify, request, render_template
import json
app = Flask(__name__)
import requests

from flask_cors import CORS
CORS(app, supports_credentials=True)

@app.route('/ebay', methods=['GET'])
def ebay():
    html_json = json.loads(request.args.get('data'))
    ebay_data = get_data_from_ebay(html_json)
    print(ebay_data)
    totalEntries = ebay_data['findItemsByKeywordsResponse'][0]['paginationOutput'][0]['totalEntries'][0]
    print(totalEntries)
    if float(totalEntries)==0:  
         re_data="No Result Found"
         print(re_data)
    else:
         
         re_data = process_data(ebay_data,totalEntries)

    print(re_data)
    return jsonify(re_data)
def get_data_from_ebay(data):
    i = 0
    kw = data['keywords']
    url = 'https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0\
&SECURITY-APPNAME=ShutangY-app-PRD-e2eba3d7e-e2362d14\
&RESPONSE-DATA-FORMAT=JSON\
&REST-PAYLOAD\
&paginationOutput.totalEntries\
&keywords=' + kw 
    lp = data['lp']
    hp = data['hp']
    if lp != 'none':
        url += '&itemFilter(' + str(i) + ').name=MinPrice' + '&itemFilter(' + str(i) + ').value=' + str(lp)+'&itemFilter('+str(i)+').paramName=Currency&itemFilter('+str(i)+').paramValue=USD'
        i += 1
    if hp != 'none':
        url += '&itemFilter(' + str(i) + ').name=MaxPrice' + '&itemFilter(' + str(i) + ').value=' + str(hp)+'&itemFilter('+str(i)+').paramName=Currency&itemFilter('+str(i)+').paramValue=USD'
        i += 1
    cd = data['condition']
    j=0
    if cd != []:
        url += '&itemFilter('+ str(i) + ').name=Condition'
        if 'New' in cd:
            url += '&itemFilter('+ str(i) + ').value('+str(j)+')=New'
            j += 1
        elif 'Used' in cd:
            url += '&itemFilter('+ str(i) + ').value('+str(j)+')=3000'
            j += 1
        elif 'Very Good' in cd:
            url += '&itemFilter('+ str(i) + ').value('+str(j)+')=4000'
            j += 1
        elif 'Good' in cd:
            url += '&itemFilter('+ str(i) + ').value('+str(j)+')=5000'
            j += 1
        else:
            url += '&itemFilter('+ str(i) + ').value('+str(j)+')=6000'
        i += 1
    seller = data['seller']
    if seller != []:
        url += '&itemFilter('+ str(i) + ').name=ReturnsAcceptedOnly&itemFilter(' + str(i) + ').value=True'
        i += 1
    ship = data['ship']
    if ship != []:
        if 'Expedited' in ship:
            url += '&itemFilter('+ str(i) + '.name=ExpeditedShippingType&itemFilter(' + str(i) + ').value=Expedited'
            i += 1
        if 'free' in ship:
            url += '&itemFilter('+ str(i) + '.name=FreeShippingOnly&itemFilter(' + str(i) + ').value=True'
            i += 1

    sort =data["option"]
    if sort=="Best Match":
       sort="BestMatch"
    elif sort=="Price:Higest":
        sort="CurrentPriceHighest"
    elif sort=="Price + Shipping: highest first":
        sort="PricePlusShippingHighest"
    else:
        sort="PricePlusShippingLowest"
     
    url += '&sortOrder='+ sort
    
    print(url)
    re = requests.get(url = url)
    data = re.json()
    return data



def process_data(data,totalEntries):
    #print(data)
    data = data['findItemsByKeywordsResponse'][0]
    ack = data['ack'][0]    # 'Success' or 'Failure'
    count = data['searchResult'][0]['@count']
    items = data['searchResult'][0]['item']
    items_arr = []
    i = 0
    for item in items:
        print('*'*30)
        print(i)
        print(item)
        item_json = {}
        try:
            title = item['title'][0]
            category_name = item['primaryCategory'][0]['categoryName'][0]
            img_url = item['galleryURL'][0]
            if img_url=="https://thumbs1.ebaystatic.com/pict/04040_0.jpg":
                img_url="https://www.csci571.com/hw/hw6/images/ebay_default.jpg"
            item_url = item['viewItemURL'][0]
            conditon = item['condition'][0]['conditionDisplayName']
            top_rating = False if 'false' in item['topRatedListing'] else True
            item_price = float(item['sellingStatus'][0]['convertedCurrentPrice'][0]['__value__'])
            free_ship = False if 'false' in item['shippingInfo'][0]['shippingType'] else True
            ship_fee = float(item['shippingInfo'][0]['shippingServiceCost'][0]['__value__'])
            seller_Return= item['returnsAccepted']
            if not seller_Return:
                seller_Return="Seller does not accept returns"
            else:
                seller_Return="Seller accepts returns"
            if ship_fee==0:
                FS="Free Shipping"
            else:
                FS="No Free Shipping"
            if item['shippingInfo'][0]['expeditedShipping']:
                FS += "-- Expedited Shipping available"
            location=item['location']
            print(item_price)
        except:
            continue
        item_json['title'] = title
        item_json['category_name'] = category_name
        item_json['img_url'] = img_url
        item_json['item_url'] = item_url
        item_json['conditon'] = conditon    #???
        item_json['top_rating'] = top_rating
        item_json['total_price'] = item_price
        item_json['ship_fee'] = ship_fee
        item_json['seller_Return'] = seller_Return
        item_json['FS'] = FS
        item_json['location']= location
        items_arr.append(item_json)
        i += 1
        if i == 10: break
    data = {}
    data['totalEntries'] = totalEntries
    data['items'] = items_arr
    return data


        
    
if __name__=='__main__':
   app.run(debug=True)
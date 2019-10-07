;
if(typeof(window.rest) === 'undefined' || window.rest == null){
    window.rest = {};
}

rest.assignMessage = function(jqXHR){
    var h = jqXHR.getResponseHeader('X-Application-Message');
    if(h == null){
        h = jqXHR.getResponseHeader('x-application-message');
    }

    if(h == 'true'){
        //adding the appMsg only if the X-Application-Message header is found.
        jqXHR.appMsg = jqXHR.responseText;
    }else{
        jqXHR.appMsg = null;
    }
};

rest.get = function(url){
    var p = new Promise(function(resolve, reject){
        jQuery.ajax({
            method: 'GET',
            url: url,
            headers: {
                'Accept': 'application/json'
            },
            success: function(d, s, j){
                resolve({data: d, status: s, jqXHR: j});                
            },
            error: function(j, s, e){
                rest.assignMessage(j);
                reject({jqXHR: j, status: s, error: e, appMsg: j.appMsg});
            }
        });
    });

    return p;
}

rest.put = function(url, data){
    var p = new Promise(function(resolve, reject){
        jQuery.ajax({
            method: 'PUT',
            url: url,
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(data),
            success: function(d, s, j){
                resolve({data: d, status: s, jqXHR: j});                
            },
            error: function(j, s, e){
                rest.assignMessage(j);
                reject({jqXHR: j, status: s, error: e, appMsg: j.appMsg});
            }
        });
    });

    return p;
}



rest.post = function(url, data){
    var p = new Promise(function(resolve, reject){
        jQuery.ajax({
            method: 'POST',
            url: url,
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(data),
            success: function(d, s, j){
                resolve({data: d, status: s, jqXHR: j});                
            },
            error: function(j, s, e){
                rest.assignMessage(j);
                reject({jqXHR: j, status: s, error: e, appMsg: j.appMsg});
            }
        });
    });

    return p;
}


rest.httpDelete = function(url){
    var p = new Promise(function(resolve, reject){
        jQuery.ajax({
            method: 'DELETE',
            url: url,
            success: function(d, s, j){
                resolve({data: d, status: s, jqXHR: j});
            },
            error: function(j, s, e){
                rest.assignMessage(j);
                reject({jqXHR: j, status: s, error: e, appMsg: j.appMsg});
            }
        });
    });

    return p;
}

rest.putAndGet = function(url, data){
    var p = new Promise(function(resolve, reject){
        rest.put(url, data).then( function(response){
            rest.handleSuccessForGet(resolve, reject, response);
        }).catch(function(response){
            reject(response);
        });
    });
    
    return p;
}

rest.postAndGet = function(url, data){
    var p = new Promise(function(resolve, reject){
        rest.post(url, data).then( function(res){
            rest.handleSuccessForGet(resolve, reject, res);
        }).catch(function(res){
            reject(res);
        });
    });
    
    return p;
}



rest.handleSuccessForGet = function(resolve, reject, response){
    var l = response.jqXHR.getResponseHeader('Location');
    if(l == null){
        //some nodejs apps send headers in lowercase.
        l = response.jqXHR.getResponseHeader('location');
    }
    if(l){
        rest.get(l).then(function(res2){
            resolve(res2);
        }).catch(function(res2){
            reject(res2);
        });
    }else{
        if(console){
            console.error('No Location header found.');
        }
    }
}